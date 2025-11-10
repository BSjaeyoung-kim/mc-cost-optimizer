package com.mcmp.azure.vm.rightsizer.batch;

import com.mcmp.azure.vm.rightsizer.client.AlarmServiceClient;
import com.mcmp.azure.vm.rightsizer.dto.AlarmHistoryDto;
import com.mcmp.azure.vm.rightsizer.dto.RecommendVmTypeDto;
import com.mcmp.azure.vm.rightsizer.mapper.AlarmHistoryMapper;
import com.mcmp.azure.vm.rightsizer.mapper.ServiceGroupMetaMapper;
import com.mcmp.azure.vm.rightsizer.properties.AzureCredentialProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

@Slf4j
@StepScope
@Component
@RequiredArgsConstructor
public class RecommendVmListItemWriter implements ItemWriter<RecommendVmTypeDto> {

    private final AzureCredentialProperties azureCredentialProperties;
    private final AlarmHistoryMapper alarmHistoryMapper;
    private final AlarmServiceClient alarmServiceClient;
    private final ServiceGroupMetaMapper serviceGroupMetaMapper;

    @Override
    public void write(Chunk<? extends RecommendVmTypeDto> chunk) throws Exception {
        for (RecommendVmTypeDto recommendVmTypeDto : chunk) {
            // servicegroup_meta에서 projectCd, workspaceCd 조회
            Map<String, String> meta = serviceGroupMetaMapper.selectProjectAndWorkspaceByVmId(
                    recommendVmTypeDto.getVmId(),
                    azureCredentialProperties.getSubscriptionId()
            );

            String projectCd = (meta != null && meta.get("projectCd") != null)
                    ? meta.get("projectCd")
                    : "default";

            String plan = recommendVmTypeDto.getPlan(); // "Up" or "Down"

            // AlarmGuideGrid.vue 파일을 보고 TYPE을 임시로 작성한다.
            AlarmHistoryDto alarmHistoryDto = AlarmHistoryDto.builder()
                    .alarmType(List.of("mail"))
                    // Abnormal (비정상), Resize(사이즈 변경), Unused(미사용)
                    .eventType("Resize")
                    .resourceId(recommendVmTypeDto.getVmId())
                    .resourceType("Virtual Machine")
                    .occureDt(new Timestamp(System.currentTimeMillis()))
                    .accountId(azureCredentialProperties.getSubscriptionId())
                    // Caution(주의), Warning(경고), Critical(긴급)
                    .urgency("Up".equals(plan) ? "Warning" : "Caution")
                    // Up(상향), Down(하향), Unused(미사용), Modernize(최신화)
                    .plan(plan)
                    .note(String.format(
                        "Recommend %s sizing for instance (%s) from current type %s to %s.",
                        plan,
                        recommendVmTypeDto.getVmId(),
                        recommendVmTypeDto.getCurrentType(),
                        recommendVmTypeDto.getRecommendType()
                    ))
                    .occureDate(new Timestamp(System.currentTimeMillis()))
                    .cspType("AZURE")
                    .projectCd(projectCd)  // servicegroup_meta에서 조회한 값 사용
                    .build();

            // 메일 발송 (에러 처리)
            try {
                alarmServiceClient.sendOptiAlarmMail(alarmHistoryDto);
                log.info("Mail sent successfully for vmId: {}, plan: {}",
                    recommendVmTypeDto.getVmId(), plan);
            } catch (Exception e) {
                log.error("Failed to send mail for vmId: {}, plan: {}, error: {}",
                    recommendVmTypeDto.getVmId(), plan, e.getMessage());
            }

            // AlarmService에서 현재 DB history가 insert 되지 않아 넣은 코드.
//            alarmHistoryMapper.insertAlarmHistory(alarmHistoryDto);
            log.info("Saved {} Azure Vm Size {} Alarm History to database (projectCd: {})",
                    recommendVmTypeDto.getVmId(), plan, projectCd);
        }
    }
}
