package com.mcmp.assetcollector.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TagDto {

    @JsonProperty("ns_id")
    private String nsId;

    @JsonProperty("mci_id")
    private String mciId;

    @JsonProperty("vm_id")
    private String vmId;

    @Builder
    public TagDto(String nsId, String mciId, String vmId) {
        this.nsId = nsId;
        this.mciId = mciId;
        this.vmId = vmId;
    }
}
