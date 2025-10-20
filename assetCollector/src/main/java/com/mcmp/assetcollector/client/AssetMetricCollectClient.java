package com.mcmp.assetcollector.client;

import com.mcmp.assetcollector.dto.MonitoringResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange("/api/observability")
public interface AssetMetricCollectClient {

    // TODO: 호출 PATH 확정되면 변경 필요.
    @GetExchange("/metrics/{nsId}/{mciId}/{vmId}")
    ResponseEntity<MonitoringResponseDto> getMetrics(
            @PathVariable String nsId,
            @PathVariable String mciId,
            @PathVariable String vmId);
}
