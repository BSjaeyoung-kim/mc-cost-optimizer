package com.mcmp.assetcollector.schedule;

import com.mcmp.assetcollector.batch.AssetBatchConstants;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ScheduleConfig {

    @Value("${assetCollectBatchCronSchedule}")
    private String assetSchedule;

    @Bean
    public JobDetail assetJobDetail() {
        return JobBuilder.newJob().ofType(AssetCltJob.class)
                .storeDurably()
                .withIdentity(AssetBatchConstants.ASSET_COLLECT_JOB, "assetCollect")
                .withDescription("Execute Spring Batch AssetCollect Batch Job with Quartz")
                .build();
    }

    @Bean
    public Trigger assetJobTrigger(JobDetail assetJobDetail) {
        CronScheduleBuilder cronSchedule = CronScheduleBuilder.cronSchedule(assetSchedule);

        return TriggerBuilder.newTrigger().forJob(assetJobDetail)
                .withIdentity("assetCollectJobTrigger1", "assetCollect")
                .withDescription("AssetCollect Job Trigger")
                .withSchedule(cronSchedule)
                .build();
    }

}
