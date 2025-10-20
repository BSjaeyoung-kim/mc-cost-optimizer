package com.mcmp.assetcollector.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MonitoringResponseDto {

    @JsonProperty("rs_code")
    private String rsCode;

    @JsonProperty("rs_msg")
    private String rsMsg;

    @JsonProperty("data")
    private List<MetricDataDto> data;

    @JsonProperty("error_message")
    private String errorMessage;

    @Builder
    public MonitoringResponseDto(String rsCode, String rsMsg, List<MetricDataDto> data, String errorMessage) {
        this.rsCode = rsCode;
        this.rsMsg = rsMsg;
        this.data = data;
        this.errorMessage = errorMessage;
    }
}
