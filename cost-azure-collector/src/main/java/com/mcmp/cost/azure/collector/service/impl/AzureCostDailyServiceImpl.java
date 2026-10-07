package com.mcmp.cost.azure.collector.service.impl;

import com.azure.core.management.profile.AzureProfile;
import com.azure.identity.ClientSecretCredential;
import com.azure.resourcemanager.AzureResourceManager;
import com.azure.resourcemanager.compute.models.VirtualMachine;
import com.azure.resourcemanager.costmanagement.CostManagementManager;
import com.azure.resourcemanager.costmanagement.models.QueryDefinition;
import com.azure.resourcemanager.costmanagement.models.QueryResult;
import com.mcmp.cost.azure.collector.dto.AzureApiCredentialDto;
import com.mcmp.cost.azure.collector.entity.AzureCostResourceDaily;
import com.mcmp.cost.azure.collector.entity.AzureCostServiceDaily;
import com.mcmp.cost.azure.collector.entity.AzureCostVmDaily;
import com.mcmp.cost.azure.collector.properties.AzureSslProperties;
import com.mcmp.cost.azure.collector.service.AzureCostDailyService;
import com.mcmp.cost.azure.collector.utils.AzureUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AzureCostDailyServiceImpl implements AzureCostDailyService {

    private final AzureSslProperties azureSslProperties;

    @Override
    public List<AzureCostServiceDaily> getCostByService(AzureApiCredentialDto azureApiCredentialDto) {
        // 0. 인증 생성
        ClientSecretCredential credential = AzureUtils.buildCredential(azureApiCredentialDto, azureSslProperties.isDisabled());

        // 1. Profile 생성
        AzureProfile profile = AzureUtils.buildProfile(azureApiCredentialDto);

        // 2. CostManagementManager 생성
        CostManagementManager costManager = CostManagementManager.authenticate(credential, profile);

        // 3. QueryDefinition 작성
        QueryDefinition query = AzureUtils.getQueryCostByServieName();

        // 4. scope 정의
        String scope = "/subscriptions/" + azureApiCredentialDto.getSubscriptionId();

        // 5. API 호출
        QueryResult queryResult = costManager
                .queries()
                .usage(scope, query);

        // 6. DB Insert
        List<AzureCostServiceDaily> azureCostServiceDailyList = new ArrayList<>();
        for (List<Object> row : queryResult.rows()) {
            AzureCostServiceDaily azureCostServiceDaily = AzureCostServiceDaily.builder()
                    .tenantId(azureApiCredentialDto.getTenantId())
                    .subscriptionId(azureApiCredentialDto.getSubscriptionId())
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
    public List<AzureCostVmDaily> getCostByVirtualMachines(AzureApiCredentialDto azureApiCredentialDto) {
        // 0. 인증 생성
        ClientSecretCredential credential = AzureUtils.buildCredential(azureApiCredentialDto, azureSslProperties.isDisabled());

        // 1. Profile 생성
        AzureProfile profile = AzureUtils.buildProfile(azureApiCredentialDto);

        // 2. CostManagementManager 생성
        CostManagementManager costManager = CostManagementManager.authenticate(credential, profile);

        // 3. QueryDefinition 작성
        QueryDefinition query = AzureUtils.getQueryCostByVirtualMachines();

        // 4. scope 정의
        String scope = "/subscriptions/" + azureApiCredentialDto.getSubscriptionId();

        // 5. API 호출
        QueryResult queryResult = costManager
                .queries()
                .usage(scope, query);

        AzureResourceManager azureResourceManager = AzureResourceManager.authenticate(credential, profile)
                .withSubscription(azureApiCredentialDto.getSubscriptionId());

        // 6. DB Insert
        List<AzureCostVmDaily> azureCostVmDailyList = new ArrayList<>();
        for (List<Object> row : queryResult.rows()) {
            String resourceId = row.get(3).toString();

            try {
                // 7. VM 정보 조회.
                VirtualMachine vm = azureResourceManager.virtualMachines().getById(resourceId);

                AzureCostVmDaily azureCostVmDaily = AzureCostVmDaily.builder()
                        .tenantId(azureApiCredentialDto.getTenantId())
                        .subscriptionId(azureApiCredentialDto.getSubscriptionId())
                        .preTaxCost((double) row.get(0))
                        .usageDate(row.get(1).toString())
                        .resourceGroupName(row.get(2).toString())
                        .resourceId(resourceId)
                        .region(vm.regionName())
                        .instanceType(vm.size().getValue())
                        .osType(vm.osType().name())
                        .vmId(vm.name())
                        .resourceGuid(row.get(4).toString())
                        .currency(row.get(5).toString())
                        .build();
                azureCostVmDailyList.add(azureCostVmDaily);
                log.debug("azureCostVmDaily data: {}", azureCostVmDaily.toString());
            } catch (Exception e) {
                log.warn("VM not found (possibly deleted): {}, skipping...", resourceId);
            }
        }
        return azureCostVmDailyList;
    }

    @Override
    public List<AzureCostResourceDaily> getCostByResources(AzureApiCredentialDto azureApiCredentialDto) {
        // 0. 인증 생성
        ClientSecretCredential credential = AzureUtils.buildCredential(azureApiCredentialDto, azureSslProperties.isDisabled());

        // 1. Profile 생성
        AzureProfile profile = AzureUtils.buildProfile(azureApiCredentialDto);

        // 2. CostManagementManager 생성
        CostManagementManager costManager = CostManagementManager.authenticate(credential, profile);

        // 3. QueryDefinition 작성 (ResourceType IN (managedclusters, storageaccounts))
        QueryDefinition query = AzureUtils.getQueryCostByResources();

        // 4. scope 정의
        String scope = "/subscriptions/" + azureApiCredentialDto.getSubscriptionId();

        // 5. API 호출 — 실패(429/5xx/인증) 시 빈 목록으로 끝내 step 은 성공시키고 다음 날 재시도한다
        QueryResult queryResult;
        try {
            queryResult = costManager
                    .queries()
                    .usage(scope, query);
        } catch (Exception e) {
            log.error("Azure resource(K8S/Object Storage) cost query failed. subscription={}, reason={}",
                    azureApiCredentialDto.getSubscriptionId(), e.getMessage(), e);
            return new ArrayList<>();
        }
        if (queryResult.nextLink() != null) {
            // SDK 1.0.0 은 nextLink 추적 API 가 없다. 필터가 좁아(구독당 AKS/스토리지 계정 수) 한 페이지로 충분하다고 가정하고 경고만 남긴다.
            log.warn("Azure resource cost query is paged; only the first page is processed. subscription={}, nextLink={}",
                    azureApiCredentialDto.getSubscriptionId(), queryResult.nextLink());
        }

        // 6. 매핑 (VM 경로와 달리 getById 조회 없음)
        List<AzureCostResourceDaily> list = AzureCostResourceRowMapper.toEntities(queryResult, azureApiCredentialDto);
        log.info("Azure resource cost rows: fetched={}, mapped={}, subscription={}",
                queryResult.rows() == null ? 0 : queryResult.rows().size(), list.size(), azureApiCredentialDto.getSubscriptionId());
        for (AzureCostResourceDaily row : list) {
            log.debug("azureCostResourceDaily data: {}", row);
        }
        return list;
    }
}
