package com.mcmp.collector.schedule;

import com.mcmp.collector.batch.UnusedCollect;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ScheduleConfig {

    @Value("${unusedBatchCronSchedule}")
    public String batchCron ;

    @Value("${curBatchCronSchedule}")
    public String curBatchCron;

    @Bean
    public JobDetail UnusedJobDetail() {
        return JobBuilder.newJob().ofType(ScheduleJob.class)
                .storeDurably()
                .withIdentity("UnusedJob", "Unused")
                .withDescription("Execute Spring Batch Job with Quartz")
                .build();
    }

    @Bean
    public Trigger sampleJobTrigger(JobDetail UnusedJobDetail) {
        CronScheduleBuilder cronSchedule = CronScheduleBuilder.cronSchedule(batchCron);

        return TriggerBuilder.newTrigger().forJob(UnusedJobDetail)
                .withIdentity("UnusedJobTrigger1", "Unused")
                .withDescription("Unused Job Trigger")
                .withSchedule(cronSchedule)
                .build();
    }

    @Bean
    public JobDetail CurJobDetail() {
        return JobBuilder.newJob().ofType(CurScheduleJob.class)
                .storeDurably()
                .withIdentity("CurJob", "Cur")
                .withDescription("Execute Spring Batch Cur Batch Job with Quartz")
                .build();
    }

    @Bean
    public Trigger CurJobTrigger(JobDetail CurJobDetail) {
        CronScheduleBuilder cronSchedule = CronScheduleBuilder.cronSchedule(curBatchCron);

        return TriggerBuilder.newTrigger().forJob(CurJobDetail)
                .withIdentity("CurJobTrigger1", "Cur")
                .withDescription("Unused Job Trigger")
                .withSchedule(cronSchedule)
                .build();
    }
}
