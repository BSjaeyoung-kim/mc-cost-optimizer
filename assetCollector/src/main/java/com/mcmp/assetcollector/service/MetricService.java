package com.mcmp.assetcollector.service;

import com.mcmp.assetcollector.dto.AssetComputeMetricDto;
import com.mcmp.assetcollector.dto.MonitoringResponseDto;
import java.util.List;

public interface MetricService {

    List<AssetComputeMetricDto> convertMetricDto(MonitoringResponseDto monitoringResponseDto);
}
