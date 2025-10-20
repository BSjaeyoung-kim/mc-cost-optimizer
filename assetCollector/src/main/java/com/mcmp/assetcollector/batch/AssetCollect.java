package com.mcmp.assetcollector.batch;

import com.mcmp.assetcollector.client.AssetMetricCollectClient;
import com.mcmp.assetcollector.dto.AssetComputeMetricDto;
import com.mcmp.assetcollector.model.batch.RunningInstanceModel;
import com.mcmp.assetcollector.service.MetricService;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.batch.MyBatisPagingItemReader;
import org.mybatis.spring.batch.builder.MyBatisPagingItemReaderBuilder;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.skip.AlwaysSkipItemSkipPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import java.util.List;

@Slf4j
@Configuration
public class AssetCollect {

    private final UpdateMetaBeforeAssetJob updateMetaBeforeAssetJob;
    private final MetricService metricService;
    private final AssetMetricCollectClient assetMetricCollectClient;
    private final SqlSessionFactory sqlSessionBatch;

    @Autowired
    public AssetCollect(UpdateMetaBeforeAssetJob updateMetaBeforeAssetJob,
                        MetricService metricService,
                        AssetMetricCollectClient assetMetricCollectClient,
                        @Qualifier("sqlSessionBatch") SqlSessionFactory sqlSessionBatch) {
        this.updateMetaBeforeAssetJob = updateMetaBeforeAssetJob;
        this.metricService = metricService;
        this.assetMetricCollectClient = assetMetricCollectClient;
        this.sqlSessionBatch = sqlSessionBatch;
    }

    @Bean
    public SkipLogListener skipLogListener() {
        return new SkipLogListener();
    }

    @Bean
    public Job assetCollectJob(JobRepository jobRepository, Step assetCollectStep) {
        return new JobBuilder(AssetBatchConstants.ASSET_COLLECT_JOB, jobRepository)
                .start(assetCollectStep)
                .listener(updateMetaBeforeAssetJob)
                .build();
    }

    @Bean
    public Step assetCollectStep(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager) {
        return new StepBuilder(AssetBatchConstants.ASSET_COLLECT_STEP, jobRepository)
                .<RunningInstanceModel, List<AssetComputeMetricDto>>chunk(1, platformTransactionManager)
                .reader(runningInstanceItemReader())
                .processor(metricCollectorProcessor())
                .writer(assetComputeMetricWriter())
                .startLimit(2)
                .faultTolerant()
                .retry(Exception.class)
                .retryLimit(2)
                .skip(Exception.class)
                .skipPolicy(new AlwaysSkipItemSkipPolicy())
                .listener(skipLogListener())
                .build();
    }

    @Bean
    @StepScope
    public MyBatisPagingItemReader<RunningInstanceModel> runningInstanceItemReader() {
        return new MyBatisPagingItemReaderBuilder<RunningInstanceModel>()
                .sqlSessionFactory(sqlSessionBatch)
                .queryId("asset.getAssetRunningInstance")
                .pageSize(1)
                .build();
    }

    @Bean
    @StepScope
    public MetricCollectorProcessor metricCollectorProcessor() {
        return new MetricCollectorProcessor(metricService, assetMetricCollectClient);
    }

    @Bean
    @StepScope
    public AssetComputeMetricWriter assetComputeMetricWriter() {
        return new AssetComputeMetricWriter(sqlSessionBatch);
    }

}
