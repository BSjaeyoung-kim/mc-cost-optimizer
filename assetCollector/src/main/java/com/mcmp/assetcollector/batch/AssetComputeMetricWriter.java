package com.mcmp.assetcollector.batch;

import com.mcmp.assetcollector.dto.AssetComputeMetricDto;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.batch.MyBatisBatchItemWriter;
import org.mybatis.spring.batch.builder.MyBatisBatchItemWriterBuilder;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import java.util.List;

@Slf4j
public class AssetComputeMetricWriter implements ItemWriter<List<AssetComputeMetricDto>> {

    private final SqlSessionFactory sqlSessionBatch;

    public AssetComputeMetricWriter(SqlSessionFactory sqlSessionBatch) {
        this.sqlSessionBatch = sqlSessionBatch;
    }

    private MyBatisBatchItemWriter<AssetComputeMetricDto> delegate;

    @PostConstruct
    public void init() {
        delegate = new MyBatisBatchItemWriterBuilder<AssetComputeMetricDto>()
                .sqlSessionFactory(sqlSessionBatch)
                .statementId("asset.insertAssetComputeMetric")
                .build();
    }

    @Override
    public void write(Chunk<? extends List<AssetComputeMetricDto>> chunks) throws Exception {
        for (List<AssetComputeMetricDto> chunk : chunks) {
            if (chunk != null && !chunk.isEmpty()) {
                delegate.write(new Chunk<>(chunk));
                log.debug("Written {} metric records", chunk.size());
            }
        }
    }
}
