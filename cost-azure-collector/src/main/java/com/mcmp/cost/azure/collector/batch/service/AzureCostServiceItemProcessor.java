package com.mcmp.cost.azure.collector.batch.service;

import com.mcmp.cost.azure.collector.entity.AzureApiCredential;
import com.mcmp.cost.azure.collector.entity.AzureCostServiceDaily;
import com.mcmp.cost.azure.collector.service.AzureCostDailyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class AzureCostServiceItemProcessor implements ItemProcessor<AzureApiCredential, List<AzureCostServiceDaily>> {

    private final AzureCostDailyService azureCostDailyService;

    @Override
    public List<AzureCostServiceDaily> process(AzureApiCredential azureApiCredential) throws Exception {
        log.info("Processing Azure cost data for tenant: {}", azureApiCredential.getTenantId());
        return azureCostDailyService.getCostByService(azureApiCredential);
    }
}
