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
public class MetricDataDto {

    @JsonProperty("name")
    private String name;

    @JsonProperty("columns")
    private List<String> columns;

    @JsonProperty("tags")
    private TagDto tags;

    @JsonProperty("values")
    private List<List<Object>> values;

    @Builder
    public MetricDataDto(String name, List<String> columns, TagDto tags, List<List<Object>> values) {
        this.name = name;
        this.columns = columns;
        this.tags = tags;
        this.values = values;
    }
}
