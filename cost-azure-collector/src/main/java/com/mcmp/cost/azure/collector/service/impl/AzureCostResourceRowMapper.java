package com.mcmp.cost.azure.collector.service.impl;

import com.azure.resourcemanager.costmanagement.models.QueryResult;
import com.mcmp.cost.azure.collector.dto.AzureApiCredentialDto;
import com.mcmp.cost.azure.collector.entity.AzureCostResourceDaily;
import com.mcmp.cost.azure.collector.utils.AzureResourceType;
import com.mcmp.cost.azure.collector.utils.AzureUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/**
 * Cost Management QueryResult(자원 단위) → {@link AzureCostResourceDaily} 매핑. Azure 호출 없이 단위테스트 가능하도록 분리.
 * 컬럼은 고정 인덱스가 아니라 이름으로 해석한다(VM 경로의 row.get(n) 방식과 다름).
 */
@Slf4j
final class AzureCostResourceRowMapper {

    private AzureCostResourceRowMapper() {}

    static List<AzureCostResourceDaily> toEntities(QueryResult queryResult, AzureApiCredentialDto dto) {
        if (queryResult == null || queryResult.rows() == null) return List.of();
        Map<String, Integer> col = AzureUtils.columnIndex(queryResult);
        requireColumns(col, "PreTaxCost", "UsageDate", "ResourceId", "Currency");

        Map<String, AzureCostResourceDaily> merged = new LinkedHashMap<>();   // key = resourceId|usageDate
        int skipped = 0;
        for (List<Object> row : queryResult.rows()) {
            String resourceId = str(row.get(col.get("ResourceId")));           // 소문자 원문 그대로
            Optional<AzureResourceType> type = AzureResourceType.fromResourceId(resourceId);
            if (type.isEmpty()) {
                skipped++;
                log.debug("skip non-target resource: {}", resourceId);
                continue;
            }

            String usageDate = usageDate(row.get(col.get("UsageDate")));
            double cost = ((Number) row.get(col.get("PreTaxCost"))).doubleValue();
            String key = resourceId + "|" + usageDate;
            AzureCostResourceDaily dup = merged.get(key);
            if (dup != null) {                                                   // 동일 자원·일 복수 row → 합산
                dup.setPreTaxCost(dup.getPreTaxCost() + cost);
                continue;
            }

            String rg = col.containsKey("ResourceGroupName") ? str(row.get(col.get("ResourceGroupName"))) : "";
            String region = col.containsKey("ResourceLocation") ? str(row.get(col.get("ResourceLocation"))).toLowerCase(Locale.ROOT) : null;
            merged.put(key, AzureCostResourceDaily.builder()
                    .tenantId(dto.getTenantId())
                    .subscriptionId(dto.getSubscriptionId())
                    .preTaxCost(cost)
                    .usageDate(usageDate)
                    .resourceGroupName(rg.isEmpty() ? resourceGroupFromId(resourceId) : rg)
                    .resourceId(resourceId)
                    .resourceName(resourceId.substring(resourceId.lastIndexOf('/') + 1))
                    .resourceType(type.get().name())
                    .serviceName(type.get().getServiceName())
                    .region(region == null || region.isEmpty() ? null : region)
                    .currency(str(row.get(col.get("Currency"))))
                    .build());
        }
        if (skipped > 0) log.info("Azure resource cost: skipped {} rows outside target types", skipped);
        return new ArrayList<>(merged.values());
    }

    private static void requireColumns(Map<String, Integer> col, String... names) {
        for (String n : names) {
            if (!col.containsKey(n)) throw new IllegalStateException("Cost Management response lacks column: " + n + " (columns=" + col.keySet() + ")");
        }
    }

    private static String str(Object o) { return o == null ? "" : o.toString(); }

    /** UsageDate 는 20250903 같은 숫자로 오므로 toString 시 지수표기/소수점이 섞이지 않게 정수 변환 */
    private static String usageDate(Object o) { return (o instanceof Number n) ? String.valueOf(n.longValue()) : str(o); }

    /** /subscriptions/x/resourcegroups/{rg}/providers/... → rg */
    static String resourceGroupFromId(String id) {
        String[] seg = id.split("/");
        for (int i = 0; i < seg.length - 1; i++) {
            if ("resourcegroups".equalsIgnoreCase(seg[i])) return seg[i + 1];
        }
        return "";
    }
}
