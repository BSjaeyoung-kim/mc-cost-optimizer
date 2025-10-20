package com.mcmp.assetcollector.service.impl;

import com.mcmp.assetcollector.dto.AssetComputeMetricDto;
import com.mcmp.assetcollector.dto.MetricDataDto;
import com.mcmp.assetcollector.dto.MonitoringResponseDto;
import com.mcmp.assetcollector.dto.TagDto;
import com.mcmp.assetcollector.service.MetricService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class MetricServiceImpl implements MetricService {

    @Override
    public List<AssetComputeMetricDto> convertMetricDto(MonitoringResponseDto response) {
        if (response == null || response.getData() == null || response.getData().isEmpty()) {
            throw new IllegalArgumentException("Invalid monitoring data");
        }

        MetricDataDto metricData = response.getData().get(0);
        TagDto tags = metricData.getTags();
        List<List<Object>> values = metricData.getValues();

        // Metic 정보 추출 부족 값은 다른 부분과 연동해서 채워야한다. vmId가 유니크 키
        String metricType = metricData.getName(); // cpu or mem
        String cspType = "";
        String cspAccount = "";
        String cspInstanceId = tags.getVmId();

        // 각 value를 디비데이터로 변환하여 저장
        List<AssetComputeMetricDto> assetComputeMetricDtoList = new ArrayList<>();
        for (List<Object> value : values) {
            if (value == null || value.size() < 2) {
                continue;
            }

            // value 값 변환, 1번째 값은 시간, 2번째 값은 매트릭 값.
            Timestamp timestamp = getTimestamp((String) value.get(0));
            double metricAmount = getMetricAmount(metricType, (Double) value.get(1));

            // AssetComputeMetric DTO 생성
            AssetComputeMetricDto metric = AssetComputeMetricDto.builder()
                    .cspType(cspType)
                    .cspAccount(cspAccount)
                    .cspInstanceId(cspInstanceId)
                    .collectDt(timestamp)
                    .metricType(metricType)
                    .metricAmount(metricAmount)
                    .resourceStatus("running")
                    .resourceSpotYn("N")
                    .build();
            log.info("convert metric data: {} ", metric);
            // DB에 저장
            assetComputeMetricDtoList.add(metric);
        }
        return assetComputeMetricDtoList;
    }

    private static Timestamp getTimestamp(String value) {
        OffsetDateTime offsetDateTime = OffsetDateTime.parse(
                value,
                DateTimeFormatter.ISO_DATE_TIME
        );
        return Timestamp.valueOf(
                offsetDateTime.atZoneSameInstant(ZoneOffset.UTC).toLocalDateTime());
    }

    private double getMetricAmount(String metricType, Double value) {
        if (Objects.equals(metricType, "cpu")) {
            return 100 - value;
        } else {
            // mem
            return value;
        }
    }
}
