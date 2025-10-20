package com.mcmp.assetcollector.contorller;

import com.mcmp.assetcollector.batch.AssetBatchConstants;
import com.mcmp.assetcollector.batch.BatchExecutorService;
import com.mcmp.assetcollector.client.AssetMetricCollectClient;
import com.mcmp.assetcollector.client.CostOptiClient;
import com.mcmp.assetcollector.dto.MonitoringResponseDto;
import com.mcmp.assetcollector.service.MetricService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api")
@Slf4j
public class HelloController {

    private final MetricService metricService;
    private final BatchExecutorService batchExecutorService;
    private final AssetMetricCollectClient assetMetricCollectClient;
    private final CostOptiClient costOptiClient;

    @GetMapping(value = "/metric/cpu/test")
    public ResponseEntity<String> testCpu() {
        // TODO: METIC 서비스가 연동되면 이부분 교체 필요.
        // MonitoringResponseDto cpuMetric = assetMetricCollectClient.getMetrics();
        MonitoringResponseDto cpuMetric = MonitoringSampleData.createCpuSampleData();

        metricService.convertMetricDto(cpuMetric);
        return ResponseEntity.ok("Hello World Metrics.");
    }

    @GetMapping(value = "/metric/mem/test")
    public ResponseEntity<String> testMem() {
        // TODO: METIC 서비스가 연동되면 이부분 교체 필요.
        // MonitoringResponseDto memMetric = assetMetricCollectClient.getMetrics();
        MonitoringResponseDto memMetric = MonitoringSampleData.createMemSampleData();

        metricService.convertMetricDto(memMetric);
        return ResponseEntity.ok("Hello World Metrics.");
    }

    @GetMapping(value = "/metric/batch/cpu/test")
    public ResponseEntity<String> testBatchCpu() {
        batchExecutorService.executeBatch(AssetBatchConstants.ASSET_COLLECT_JOB);
        return ResponseEntity.ok("Hello World Metrics.");
    }

    @GetMapping(value = "/metric/client/test")
    public ResponseEntity<String> testClientCall() {
        try {
            assetMetricCollectClient.getMetrics("a", "b", "c");
            log.info("observability api call successfully");
        } catch (Exception e) {
            log.warn("Failed to api call: {}", e.getMessage());
            // 에러를 무시하고 계속 진행, 현재 연동 서비스를 테스트 할 수 없음.
        }
        try {
            costOptiClient.updateSvcGrpMeta();
            log.info("Cost optimization metadata api call successfully");
        } catch (Exception e) {
            log.warn("Failed to api call. Service may be unavailable: {}", e.getMessage());
            // 에러를 무시하고 계속 진행, 현재 연동 서비스를 테스트 할 수 없음.
        }
        return ResponseEntity.ok("Hello World Metrics.");
    }


}
