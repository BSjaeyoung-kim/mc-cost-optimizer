package com.mcmp.assetcollector.batch;

import com.mcmp.assetcollector.client.AssetMetricCollectClient;
import com.mcmp.assetcollector.contorller.MonitoringSampleData;
import com.mcmp.assetcollector.dto.AssetComputeMetricDto;
import com.mcmp.assetcollector.dto.MonitoringResponseDto;
import com.mcmp.assetcollector.model.batch.RunningInstanceModel;
import com.mcmp.assetcollector.service.MetricService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class MetricCollectorProcessor implements ItemProcessor<RunningInstanceModel, List<AssetComputeMetricDto>> {

    private final MetricService metricService;
    private final AssetMetricCollectClient assetMetricCollectClient;

    @Override
    public List<AssetComputeMetricDto> process(RunningInstanceModel item) throws Exception {
        // TODO: 매트릭 조회 현재는 연동되지 않으므로 CPU sample Data를 조회
        // MonitoringResponseDto response = assetMetricCollectClient.getMetrics(
        //        item.getNsID(),
        //        item.getMciID(),
        //        item.getVmID()).getBody();
        MonitoringResponseDto response = MonitoringSampleData.createCpuSampleData();

        if (response == null || response.getData() == null || response.getData().isEmpty()) {
            return null;
        }

        // DB 데이터로 변환
        return metricService.convertMetricDto(response);
    }
}
