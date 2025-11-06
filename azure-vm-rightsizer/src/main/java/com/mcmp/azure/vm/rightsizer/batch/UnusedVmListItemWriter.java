package com.mcmp.azure.vm.rightsizer.batch;

import com.mcmp.azure.vm.rightsizer.client.AlarmServiceClient;
import com.mcmp.azure.vm.rightsizer.dto.AlarmHistoryDto;
import com.mcmp.azure.vm.rightsizer.dto.UnusedVmDto;
import com.mcmp.azure.vm.rightsizer.mapper.AlarmHistoryMapper;
import com.mcmp.azure.vm.rightsizer.properties.AzureCredentialProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;
import java.sql.Timestamp;
import java.util.List;

/**
 * Unused VM 알림 발송 ItemWriter
 */
@Slf4j
@StepScope
@Component
@RequiredArgsConstructor
public class UnusedVmListItemWriter implements ItemWriter<UnusedVmDto> {

    private final AzureCredentialProperties azureCredentialProperties;
    private final AlarmHistoryMapper alarmHistoryMapper;
    private final AlarmServiceClient alarmServiceClient;

    @Override
    public void write(Chunk<? extends UnusedVmDto> chunk) throws Exception {
        for (UnusedVmDto unusedVm : chunk) {
            if (unusedVm.getUnusedRating() == null) {
                continue;  // 등급이 없는 경우 skip
            }

            // Unused 메시지 생성
            String note = String.format(
                "VM(%s)의 CPU 사용률이 지난 14일간 평균 %.2f%%, 최대 %.2f%%로 매우 낮습니다. " +
                "Unused 자원일 가능성이 있으니 확인이 필요합니다. ",
                unusedVm.getVmId(),
                unusedVm.getAvgCpu14Days(),
                unusedVm.getMaxCpu14Days()
            );

            AlarmHistoryDto alarmHistoryDto = AlarmHistoryDto.builder()
                .alarmType(List.of("mail"))
                // Abnormal (비정상), Resize(사이즈 변경), Unused(미사용)
                .eventType("Unused")
                .resourceId(unusedVm.getVmId())
                .resourceType("Virtual Machine")
                .occureDt(new Timestamp(System.currentTimeMillis()))
                .accountId(azureCredentialProperties.getSubscriptionId())
                // Unused는 항상 Warning 등급
                .urgency("Warning")
                .plan("Warning")
                .note(note)
                .occureDate(new Timestamp(System.currentTimeMillis()))
                .cspType("AZURE")
                .projectCd(unusedVm.getProjectCd())
                .build();

            // 알림 발송 (알림 서비스 URL 미설정 시 skip)
            try {
                alarmServiceClient.sendOptiAlarmMail(alarmHistoryDto);
                log.info("Sent unused alarm notification for VM: {}", unusedVm.getVmId());
            } catch (Exception e) {
                log.warn("Failed to send unused alarm notification for VM: {} - {}",
                    unusedVm.getVmId(), e.getMessage());
            }

            // AlarmService에서 현재 DB history가 insert 되지 않아 넣은 코드.
            alarmHistoryMapper.insertAlarmHistory(alarmHistoryDto);
            log.info("Saved {} Azure Unused Alarm History to database (project: {}, avg: {}%, max: {}%)",
                unusedVm.getVmId(), unusedVm.getProjectCd(),
                String.format("%.2f", unusedVm.getAvgCpu14Days()),
                String.format("%.2f", unusedVm.getMaxCpu14Days()));
        }
    }
}
