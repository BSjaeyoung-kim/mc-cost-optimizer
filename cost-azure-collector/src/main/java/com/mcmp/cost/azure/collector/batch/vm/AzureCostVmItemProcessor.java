package com.mcmp.cost.azure.collector.batch.vm;

import com.mcmp.cost.azure.collector.entity.AzureApiCredential;
import com.mcmp.cost.azure.collector.entity.AzureCostVmDaily;
import com.mcmp.cost.azure.collector.service.AzureCostDailyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AzureCostVmItemProcessor implements ItemProcessor<AzureApiCredential, List<AzureCostVmDaily>> {

    private final AzureCostDailyService azureCostDailyService;

    @Override
    public List<AzureCostVmDaily> process(AzureApiCredential azureApiCredential) throws Exception {
        log.info("Processing Azure cost data for tenant: {}", azureApiCredential.getTenantId());

        return azureCostDailyService.getCostByVirtualMachines(azureApiCredential);
    }
}
