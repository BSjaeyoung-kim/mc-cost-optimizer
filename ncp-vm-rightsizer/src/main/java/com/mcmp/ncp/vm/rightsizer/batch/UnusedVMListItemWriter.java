package com.mcmp.ncp.vm.rightsizer.batch;

import com.mcmp.ncp.vm.rightsizer.client.AlarmServiceClient;
import com.mcmp.ncp.vm.rightsizer.dto.AlarmHistoryDto;
import com.mcmp.ncp.vm.rightsizer.dto.UnusedDto;
import com.mcmp.ncp.vm.rightsizer.mapper.AlarmHistoryMapper;
import com.mcmp.ncp.vm.rightsizer.properties.NcpCredentialProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;
import java.sql.Timestamp;
import java.util.List;

/**
 * Unused Instance 알림 발송 ItemWriter
 */
@Slf4j
@StepScope
@Component
@RequiredArgsConstructor
public class UnusedVMListItemWriter implements ItemWriter<UnusedDto> {

    private final NcpCredentialProperties ncpCredentialProperties;
    private final AlarmHistoryMapper alarmHistoryMapper;
    private final AlarmServiceClient alarmServiceClient;

    @Override
    public void write(Chunk<? extends UnusedDto> chunk) throws Exception {
        for (UnusedDto unusedDto : chunk) {
            if (unusedDto.getUnusedRating() == null) {
                continue;  // 등급이 없는 경우 skip
            }

            // Unused 메시지 생성
            String note = String.format(
                "Instance (%s) has very low CPU utilization over the past 14 days (average: %.2f%%, maximum: %.2f%%). " +
                "This resource may be unused and requires verification.",
                unusedDto.getInstanceNo(),
                unusedDto.getAvgCpu14Days(),
                unusedDto.getMaxCpu14Days()
            );

            AlarmHistoryDto alarmHistoryDto = AlarmHistoryDto.builder()
                .alarmType(List.of("mail"))
                // Abnormal (비정상), Resize(사이즈 변경), Unused(미사용)
                .eventType("Unused")
                .resourceId(unusedDto.getInstanceNo())
                .resourceType("Server")
                .occureDt(new Timestamp(System.currentTimeMillis()))
                .accountId(unusedDto.getMemberNo())
                // Unused는 항상 Warning 등급
                .urgency("Warning")
                .plan("Warning")
                .note(note)
                .occureDate(new Timestamp(System.currentTimeMillis()))
                .cspType("NCP")
                .projectCd(unusedDto.getProjectCd())
                .build();

            // 알림 발송 (알림 서비스 URL 미설정 시 skip)
            try {
                alarmServiceClient.sendOptiAlarmMail(alarmHistoryDto);
                log.info("Sent unused alarm notification for Instance: {}", unusedDto.getInstanceNo());
            } catch (Exception e) {
                log.warn("Failed to send unused alarm notification for Instance: {} - {}",
                    unusedDto.getInstanceNo(), e.getMessage());
            }

            // AlarmService에서 현재 DB history가 insert 되지 않아 넣은 코드.
            alarmHistoryMapper.insertAlarmHistory(alarmHistoryDto);
            log.info("Saved {} NCP Unused Alarm History to database (project: {}, avg: {}%, max: {}%)",
                unusedDto.getInstanceNo(), unusedDto.getProjectCd(),
                String.format("%.2f", unusedDto.getAvgCpu14Days()),
                String.format("%.2f", unusedDto.getMaxCpu14Days()));
        }
    }
}
