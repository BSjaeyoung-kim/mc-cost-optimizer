package com.mcmp.cost.ncp.collector.service.impl;

import com.mcmp.cost.ncp.collector.config.RestTemplateConfig;
import com.mcmp.cost.ncp.collector.constants.NcpApiUrl;
import com.mcmp.cost.ncp.collector.dto.ContractDemandCost;
import com.mcmp.cost.ncp.collector.dto.ContractDemandCostList;
import com.mcmp.cost.ncp.collector.dto.ContractDemandCostListWrapper;
import com.mcmp.cost.ncp.collector.dto.ProductDemandCost;
import com.mcmp.cost.ncp.collector.dto.ProductDemandCostList;
import com.mcmp.cost.ncp.collector.dto.ProductDemandCostListWrapper;
import com.mcmp.cost.ncp.collector.entity.NcpApiCredential;
import com.mcmp.cost.ncp.collector.entity.NcpCostServiceMonth;
import com.mcmp.cost.ncp.collector.entity.NcpCostVmMonth;
import com.mcmp.cost.ncp.collector.service.NcpCostMonthService;
import com.mcmp.cost.ncp.collector.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class NcpCostMonthServiceImpl implements NcpCostMonthService {

    private final RestTemplateConfig restTemplateConfig;

    public List<NcpCostServiceMonth> getCostByService(NcpApiCredential ncpApiCredential) {
        RestTemplate restTemplate = restTemplateConfig.createRestTemplateWithKey(
                ncpApiCredential.getIamAccessKey(),
                ncpApiCredential.getIamSecretKey());

        LocalDate today = LocalDate.now(ZoneId.systemDefault());
        String yearMonth = DateUtils.getYearMonth(today);

        // 설정 기간에 따라 서비스별 청구 비용 목록을 조회 (월단위),
        String url = NcpApiUrl.PRODUCT_DEMAND_COST_LIST_URL
                + "?startMonth=" + yearMonth
                + "&endMonth=" + yearMonth
                + "&responseFormatType=json";
        log.debug("url: {}", url);

        ProductDemandCostListWrapper response = restTemplate.getForObject(url, ProductDemandCostListWrapper.class);
        ProductDemandCostList productDemandCostList = Objects.requireNonNull(response).getGetProductDemandCostListResponse();

        List<NcpCostServiceMonth> ncpCostServiceMonthList = new ArrayList<>();
        // 매일 청구 비용이 NCP에서 한국시간 8시쯤 제공한다.
        // 해당 시간에 대한 정보는 productDemandCostList 객체 내 WriteDate를 통해서 알 수 있다.
        // 수집할때, 해당 필드가 당일 날짜인지를 확인하여, 데이터를 적재할지를 판단한다.
        // 1일 인 경우, 전달 청구 금액이 전달되는 것으로 확인된다.
        ProductDemandCost todayProductDemandCost = productDemandCostList.getProductDemandCostList().getFirst();
        LocalDate givenDate = todayProductDemandCost.getWriteDate()
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        if (!givenDate.equals(today)) {
            log.info("{} 비용 데이터가 NCP에서 제공되지 않아 수집을 종료합니다.", givenDate);
            return ncpCostServiceMonthList;
        }

        for (ProductDemandCost productDemandCost : productDemandCostList.getProductDemandCostList()) {
            NcpCostServiceMonth ncpCostServiceMonth = NcpCostServiceMonth.builder()
                    .memberNo(productDemandCost.getMemberNo())
                    .demandAmount(productDemandCost.getDemandAmount())
                    .demandMonth(productDemandCost.getDemandMonth())
                    .useAmount(productDemandCost.getUseAmount())
                    .productDemandType(productDemandCost.getProductDemandType().getCodeName())
                    .writeDate(productDemandCost.getWriteDate())
                    .payCurrency(productDemandCost.getPayCurrency().getCode())
                    .build();
            log.debug("NcpCostServiceMonth: {}", ncpCostServiceMonth.toString());
            ncpCostServiceMonthList.add(ncpCostServiceMonth);
        }

        return ncpCostServiceMonthList;
    }

    @Override
    public List<NcpCostVmMonth> getCostByVm(NcpApiCredential ncpApiCredential) {
        RestTemplate restTemplate = restTemplateConfig.createRestTemplateWithKey(
                ncpApiCredential.getIamAccessKey(),
                ncpApiCredential.getIamSecretKey());

        LocalDate today = LocalDate.now(ZoneId.systemDefault());
        String yearMonth = DateUtils.getYearMonth(today);
        // 설정 기간에 따라 VM별 청구 비용을 조회 (월단위),
        String url = NcpApiUrl.CONTRACT_DEMAND_COST_LIST_URL
                + "?startMonth=" + yearMonth
                + "&endMonth=" + yearMonth
                + "&demandTypeDetailCode=" + "VSVR" // VM만 조회하는 코드.
                + "&responseFormatType=json";
        log.debug("url: {}", url);

        ContractDemandCostListWrapper response = restTemplate.getForObject(url, ContractDemandCostListWrapper.class);
        ContractDemandCostList contractDemandCostList = Objects.requireNonNull(response).getGetContractDemandCostListResponse();

        // 매일 청구 비용이 NCP에서 한국시간 8시쯤 제공한다.
        // 해당 시간에 대한 정보는 contractDemandCostList 객체 내 WriteDate를 통해서 알 수 있다.
        // 수집할때, 해당 필드가 당일 날짜인지를 확인하여, 데이터를 적재할지를 판단한다.
        // 1일 인 경우, 전달 청구 금액이 전달되는 것으로 확인된다.
        ContractDemandCost todayContractDemandCost = contractDemandCostList.getContractDemandCostList().getFirst();
        LocalDate givenDate = todayContractDemandCost.getWriteDate()
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        List<NcpCostVmMonth> ncpCostVmMonthList = new ArrayList<>();
        if (!givenDate.equals(today)) {
            log.info("{} VM 비용 데이터가 NCP에서 제공되지 않아 수집을 종료합니다.", givenDate);
            return ncpCostVmMonthList;
        }

        for (ContractDemandCost contractDemandCost : contractDemandCostList.getContractDemandCostList()) {
            NcpCostVmMonth ncpCostVmMonth = NcpCostVmMonth.builder()
                    .memberNo(contractDemandCost.getMemberNo())
                    .demandMonth(contractDemandCost.getDemandMonth())
                    .instanceNo(contractDemandCost.getContract().getContractProductList().getFirst().getInstanceNo())
                    .instanceName(contractDemandCost.getContract().getInstanceName())
                    .usageUnitCode(contractDemandCost.getUsageUnit().getCode())
                    .usageUnitName(contractDemandCost.getUsageUnit().getCodeName())
                    .productPrice(contractDemandCost.getProductPrice())
                    .unitUsageQuantity(contractDemandCost.getUnitUsageQuantity())
                    .totalUnitUsageQuantity(contractDemandCost.getTotalUnitUsageQuantity())
                    .useAmount(contractDemandCost.getUseAmount())
                    .demandAmount(contractDemandCost.getDemandAmount())
                    .writeDate(contractDemandCost.getWriteDate())
                    .payCurrency(contractDemandCost.getPayCurrency().getCode())
                    .build();
            log.debug("NcpCostVmMonth: {}", ncpCostVmMonth.toString());
            ncpCostVmMonthList.add(ncpCostVmMonth);
        }

        return ncpCostVmMonthList;
    }
}
