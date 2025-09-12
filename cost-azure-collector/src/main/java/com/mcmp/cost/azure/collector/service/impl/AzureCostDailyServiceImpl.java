package com.mcmp.cost.azure.collector.service.impl;

import com.azure.core.management.AzureEnvironment;
import com.azure.core.management.profile.AzureProfile;
import com.azure.identity.ClientSecretCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.azure.resourcemanager.costmanagement.CostManagementManager;
import com.azure.resourcemanager.costmanagement.models.ExportType;
import com.azure.resourcemanager.costmanagement.models.FunctionType;
import com.azure.resourcemanager.costmanagement.models.GranularityType;
import com.azure.resourcemanager.costmanagement.models.QueryAggregation;
import com.azure.resourcemanager.costmanagement.models.QueryColumnType;
import com.azure.resourcemanager.costmanagement.models.QueryComparisonExpression;
import com.azure.resourcemanager.costmanagement.models.QueryDataset;
import com.azure.resourcemanager.costmanagement.models.QueryDefinition;
import com.azure.resourcemanager.costmanagement.models.QueryFilter;
import com.azure.resourcemanager.costmanagement.models.QueryGrouping;
import com.azure.resourcemanager.costmanagement.models.QueryOperatorType;
import com.azure.resourcemanager.costmanagement.models.QueryResult;
import com.azure.resourcemanager.costmanagement.models.QueryTimePeriod;
import com.azure.resourcemanager.costmanagement.models.TimeframeType;
import com.mcmp.cost.azure.collector.entity.AzureApiCredential;
import com.mcmp.cost.azure.collector.entity.AzureCostServiceDaily;
import com.mcmp.cost.azure.collector.entity.AzureCostVmDaily;
import com.mcmp.cost.azure.collector.service.AzureCostDailyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class AzureCostDailyServiceImpl implements AzureCostDailyService {

    @Override
    public List<AzureCostServiceDaily> getCostByService(AzureApiCredential azureApiCredential) {
        // 0. 인증 생성
        ClientSecretCredential credential = buildCredential(azureApiCredential);

        // 1. Profile 생성
        AzureProfile profile = buildProfile(azureApiCredential);

        // 2. CostManagementManager 생성
        CostManagementManager costManager = CostManagementManager.authenticate(credential, profile);

        // 3. QueryDefinition 작성
        QueryDefinition query = getQueryCostByServieName();

        // 4. scope 정의
        String scope = "/subscriptions/" + azureApiCredential.getSubscriptionId();

        // 5. API 호출
        QueryResult queryResult = costManager
                .queries()
                .usage(scope, query);

        // 6. DB Insert
        List<AzureCostServiceDaily> azureCostServiceDailyList = new ArrayList<>();
        for (List<Object> row : queryResult.rows()) {
            AzureCostServiceDaily azureCostServiceDaily = AzureCostServiceDaily.builder()
                    .tenantId(azureApiCredential.getTenantId())
                    .subscriptionId(azureApiCredential.getSubscriptionId())
                    .preTaxCost((double) row.get(0))
                    .usageDate(row.get(1).toString())
                    .serviceName(row.get(2).toString())
                    .currency(row.get(3).toString())
                    .build();
            azureCostServiceDailyList.add(azureCostServiceDaily);
            log.debug("azureCostServiceDaily data: {}", azureCostServiceDaily.toString());
        }
        return azureCostServiceDailyList;
    }

    @Override
    public List<AzureCostVmDaily> getCostByVirtualMachines(AzureApiCredential azureApiCredential) {
        // 0. 인증 생성
        ClientSecretCredential credential = buildCredential(azureApiCredential);

        // 1. Profile 생성
        AzureProfile profile = buildProfile(azureApiCredential);

        // 2. CostManagementManager 생성
        CostManagementManager costManager = CostManagementManager.authenticate(credential, profile);

        // 3. QueryDefinition 작성
        QueryDefinition query = getQueryCostByVirtualMachines();

        // 4. scope 정의
        String scope = "/subscriptions/" + azureApiCredential.getSubscriptionId();

        // 5. API 호출
        QueryResult queryResult = costManager
                .queries()
                .usage(scope, query);

        // 6. DB Insert
        List<AzureCostVmDaily> azureCostVmDailyList = new ArrayList<>();
        for (List<Object> row : queryResult.rows()) {
            AzureCostVmDaily azureCostVmDaily = AzureCostVmDaily.builder()
                    .tenantId(azureApiCredential.getTenantId())
                    .subscriptionId(azureApiCredential.getSubscriptionId())
                    .preTaxCost((double) row.get(0))
                    .usageDate(row.get(1).toString())
                    .resourceGroupName(row.get(2).toString())
                    .resourceId(row.get(3).toString())
                    .resourceGuid(row.get(4).toString())
                    .currency(row.get(5).toString())
                    .build();
            azureCostVmDailyList.add(azureCostVmDaily);
            log.debug("azureCostVmDaily data: {}", azureCostVmDaily.toString());
        }
        return azureCostVmDailyList;
    }


    private ClientSecretCredential buildCredential(AzureApiCredential azureApiCredential) {
        return new ClientSecretCredentialBuilder()
                .tenantId(azureApiCredential.getTenantId())
                .clientId(azureApiCredential.getClientId())
                .clientSecret(azureApiCredential.getClientSecret())
                .build();
    }

    private AzureProfile buildProfile(AzureApiCredential azureApiCredential) {
        return new AzureProfile(azureApiCredential.getTenantId(),
                azureApiCredential.getSubscriptionId(),
                AzureEnvironment.AZURE);
    }

    private QueryDefinition getQueryCostByServieName() {
        // 어제 날짜 구하기
        LocalDate today = LocalDate.now().minusDays(1);
        // 시작 시간: 00:00:00
        OffsetDateTime startOfDay = today.atStartOfDay().atOffset(ZoneOffset.UTC);
        // 끝 시간: 23:59:59
        OffsetDateTime endOfDay = today.atTime(23, 59, 59).atOffset(ZoneOffset.UTC);

        return new QueryDefinition()
                .withType(ExportType.ACTUAL_COST)
                .withTimeframe(TimeframeType.CUSTOM)
                .withTimePeriod(new QueryTimePeriod()
                        .withFrom(startOfDay)
                        .withTo(endOfDay))
                .withDataset(new QueryDataset()
                        .withGranularity(GranularityType.DAILY)
                        .withAggregation(
                                Map.of(
                                        "totalCost",
                                        new QueryAggregation()
                                                .withName("PreTaxCost")
                                                .withFunction(FunctionType.SUM)
                                )
                        )
                        .withGrouping(List.of(
                                new QueryGrouping()
                                        .withType(QueryColumnType.DIMENSION)
                                        .withName("ServiceName")
                        ))
                );
    }

    private QueryDefinition getQueryCostByVirtualMachines() {
        // 어제 날짜 구하기
        LocalDate today = LocalDate.now().minusDays(1);
        // 시작 시간: 00:00:00
        OffsetDateTime startOfDay = today.atStartOfDay().atOffset(ZoneOffset.UTC);
        // 끝 시간: 23:59:59
        OffsetDateTime endOfDay = today.atTime(23, 59, 59).atOffset(ZoneOffset.UTC);

        return new QueryDefinition()
                .withType(ExportType.ACTUAL_COST)
                .withTimeframe(TimeframeType.CUSTOM)
                .withTimePeriod(new QueryTimePeriod()
                        .withFrom(startOfDay)
                        .withTo(endOfDay))
                .withDataset(new QueryDataset()
                        .withGranularity(GranularityType.DAILY)
                        .withAggregation(
                                Map.of(
                                        "totalCost",
                                        new QueryAggregation()
                                                .withName("PreTaxCost")
                                                .withFunction(FunctionType.SUM)
                                )
                        )
                        .withGrouping(List.of(
                                new QueryGrouping()
                                        .withType(QueryColumnType.DIMENSION)
                                        .withName("ResourceGroupName"),
                                new QueryGrouping()
                                        .withType(QueryColumnType.DIMENSION)
                                        .withName("ResourceId"),
                                new QueryGrouping()
                                        .withType(QueryColumnType.DIMENSION)
                                        .withName("ResourceGuid")
                        ))
                        .withFilter(new QueryFilter()
                                .withDimensions(
                                        new QueryComparisonExpression()
                                                .withName("ServiceName")
                                                .withOperator(QueryOperatorType.IN)
                                                .withValues(List.of("Virtual Machines"))
                                ))
                );
    }
}
