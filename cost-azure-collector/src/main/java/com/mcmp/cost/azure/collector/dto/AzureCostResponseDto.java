package com.mcmp.cost.azure.collector.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
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
public class AzureCostResponseDto {

    /**
     * 비용. ex) 16345.824
     */
    private Double preTaxCost;
    /**
     * 날짜. ex) 20250903
     */
    private String usageDate;

    /**
     * 서비스 이름. ex) Virtual Machines
     */
    private String serviceName;

    /**
     * 리소스 그룹. ex) rg-dongwoo-1
     */
    private String resourceGroupName;

    /**
     * 리소스 아이디.(AWS의 ARN과 비슷)
     * ex) "/subscriptions/00000000-0000-0000-0000-00000000000/resourcegroups/rg-dongwoo-1/providers/microsoft.compute/virtualmachines/vm-1"
     */
    private String resourceId;

    /**
     * 리소스 고유 아이디. ex) 00000000-0000-0000-0000-00000000000
     */
    private String resourceGuid;

    /**
     * 통화 단위. ex) KRW
     */
    private String currency;

    @Builder
    public AzureCostResponseDto(Double preTaxCost, String usageDate, String serviceName, String resourceGroupName, String resourceId, String resourceGuid, String currency) {
        this.preTaxCost = preTaxCost;
        this.usageDate = usageDate;
        this.serviceName = serviceName;
        this.resourceGroupName = resourceGroupName;
        this.resourceId = resourceId;
        this.resourceGuid = resourceGuid;
        this.currency = currency;
    }
}
