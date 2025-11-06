package com.mcmp.azure.vm.rightsizer.batch;

import com.mcmp.azure.vm.rightsizer.client.AlarmServiceClient;
import com.mcmp.azure.vm.rightsizer.dto.AlarmHistoryDto;
import com.mcmp.azure.vm.rightsizer.dto.AnomalyDto;
import com.mcmp.azure.vm.rightsizer.mapper.AlarmHistoryMapper;
import com.mcmp.azure.vm.rightsizer.mapper.DailyAbnormalByProductMapper;
import com.mcmp.azure.vm.rightsizer.properties.AzureCredentialProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;
import java.sql.Timestamp;
import java.util.List;

@Slf4j
@StepScope
@Component
@RequiredArgsConstructor
public class AnomalyVmListItemWriter implements ItemWriter<AnomalyDto> {

    private final AzureCredentialProperties azureCredentialProperties;
    private final AlarmHistoryMapper alarmHistoryMapper;
    private final DailyAbnormalByProductMapper dailyAbnormalByProductMapper;
    private final AlarmServiceClient alarmServiceClient;

    @Override
    public void write(Chunk<? extends AnomalyDto> chunk) throws Exception {
        for (AnomalyDto anomalyDto : chunk) {
            if (anomalyDto.getAbnormalRating() == null) {
                continue;  // 등급이 없는 경우 skip하고 다음 항목으로
            }

            // 등급별 메시지 생성
            String note = String.format(
                    "VM(%s)의 비용이 지난달 평균(%.2f원) 대비 %.1f%% 증가했습니다.",
                    anomalyDto.getVmId(),
                    anomalyDto.getSubjectCost(),
                    anomalyDto.getPercentagePoint()
            );

            AlarmHistoryDto alarmHistoryDto = AlarmHistoryDto.builder()
                    .alarmType(List.of("mail"))
                    // Abnormal (비정상), Resize(사이즈 변경), Unused(미사용)
                    .eventType("Abnormal")
                    .resourceId(anomalyDto.getVmId())
                    .resourceType(anomalyDto.getProductCd())
                    .occureDt(new Timestamp(System.currentTimeMillis()))
                    .accountId(azureCredentialProperties.getSubscriptionId())
                    // Caution(주의), Warning(경고), Critical(긴급)
                    .urgency(anomalyDto.getAbnormalRating())
                    .plan(anomalyDto.getAbnormalRating())
                    .note(note)
                    .occureDate(new Timestamp(System.currentTimeMillis()))
                    .cspType("AZURE")
                    .projectCd(anomalyDto.getProjectCd())  // AnomalyDto에서 가져온 값 사용 (servicegroup_meta 조인)
                    .build();

            // 알림 발송 (알림 서비스 URL 미설정 시 skip)
            try {
                alarmServiceClient.sendOptiAlarmMail(alarmHistoryDto);
                log.info("Sent alarm notification for VM: {}", anomalyDto.getVmId());
            } catch (Exception e) {
                log.warn("Failed to send alarm notification for VM: {} - {}", anomalyDto.getVmId(), e.getMessage());
            }

            // AlarmService에서 현재 DB history가 insert 되지 않아 넣은 코드.
            alarmHistoryMapper.insertAlarmHistory(alarmHistoryDto);
            // 이상비용 DB insert
            dailyAbnormalByProductMapper.insertDailyAbnormalByProduct(anomalyDto);
            log.info("Saved {} Azure Abnormal Alarm History to database (project: {}, rating: {}, percentage: {}%)",
                    anomalyDto.getVmId(), anomalyDto.getProjectCd(), anomalyDto.getAbnormalRating(),
                    String.format("%.1f", anomalyDto.getPercentagePoint()));
        }
    }
}
