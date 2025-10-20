package com.mcmp.assetcollector.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import java.sql.Timestamp;

@Getter
@Setter
@ToString
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AssetComputeMetricDto {

    private String cspType;
    private String cspAccount;
    private String cspInstanceId;
    private Timestamp collectDt;
    private String metricType;
    private double metricAmount;
    private String resourceStatus;
    private String resourceSpotYn;

    @Builder
    public AssetComputeMetricDto(String cspType, String cspAccount, String cspInstanceId, Timestamp collectDt, String metricType, double metricAmount, String resourceStatus, String resourceSpotYn) {
        this.cspType = cspType;
        this.cspAccount = cspAccount;
        this.cspInstanceId = cspInstanceId;
        this.collectDt = collectDt;
        this.metricType = metricType;
        this.metricAmount = metricAmount;
        this.resourceStatus = resourceStatus;
        this.resourceSpotYn = resourceSpotYn;
    }
}
