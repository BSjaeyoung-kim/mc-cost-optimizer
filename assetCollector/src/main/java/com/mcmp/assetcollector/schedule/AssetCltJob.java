package com.mcmp.assetcollector.schedule;

import com.mcmp.assetcollector.batch.AssetBatchConstants;
import com.mcmp.assetcollector.batch.BatchExecutorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class AssetCltJob extends QuartzJobBean {

    private final BatchExecutorService batchExecutorService;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        log.info("Starting AssetCollect Quartz Job");

        try {
            batchExecutorService.executeBatch(AssetBatchConstants.ASSET_COLLECT_JOB);
        } catch (Exception e) {
            log.error("Error executing Spring Batch AssetCollect Job", e);
            throw new JobExecutionException("Failed to execute Spring AssetCollect Batch Job", e);
        }

        log.info("AssetCollect Quartz Job completed");
    }
}
