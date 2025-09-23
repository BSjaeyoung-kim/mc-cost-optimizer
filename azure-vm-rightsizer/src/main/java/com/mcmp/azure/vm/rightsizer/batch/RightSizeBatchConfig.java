package com.mcmp.azure.vm.rightsizer.batch;

import com.mcmp.azure.vm.rightsizer.dto.AzureCostVmDailyDto;
import com.mcmp.azure.vm.rightsizer.dto.RecommendVmTypeDto;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class RightSizeBatchConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final RecommendVmListItemReader recommendVmListItemReader;
    private final RecommendVmListItemProcessor recommendVmItemProcessor;
    private final RecommendVmListItemWriter recommendVmListItemWriter;

    @Bean(name = RightSizeBatchConstants.SIZE_UP_JOB)
    public Job recommendVmJob() {
        return new JobBuilder(RightSizeBatchConstants.SIZE_UP_JOB, jobRepository)
                .start(recommendVmStep())
                .build();
    }

    @Bean(name = RightSizeBatchConstants.SIZE_UP_STEP)
    public Step recommendVmStep() {
        return new StepBuilder(RightSizeBatchConstants.SIZE_UP_STEP, jobRepository)
                .<AzureCostVmDailyDto, RecommendVmTypeDto>chunk(1, transactionManager)
                .reader(recommendVmListItemReader)
                .processor(recommendVmItemProcessor)
                .writer(recommendVmListItemWriter)
                .allowStartIfComplete(true)
                .build();
    }
}
