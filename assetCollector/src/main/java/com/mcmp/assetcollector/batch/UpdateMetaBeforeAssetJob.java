package com.mcmp.assetcollector.batch;

import com.mcmp.assetcollector.client.CostOptiClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class UpdateMetaBeforeAssetJob implements JobExecutionListener {

    private final CostOptiClient costOptiClient;

    @Override
    public void beforeJob(JobExecution jobExecution) {
        try {
            costOptiClient.updateSvcGrpMeta();
            log.info("Cost optimization metadata updated successfully");
        } catch (Exception e) {
            log.warn("Failed to update cost optimization metadata. Service may be unavailable: {}", e.getMessage());
            // 에러를 무시하고 계속 진행, 현재 연동 서비스를 테스트 할 수 없음.
        }
    }
}
