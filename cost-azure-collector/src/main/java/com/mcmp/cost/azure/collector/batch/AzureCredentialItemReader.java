package com.mcmp.cost.azure.collector.batch;

import com.mcmp.cost.azure.collector.dto.AzureApiCredentialDto;
import com.mcmp.cost.azure.collector.properties.AzureCredentialProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.stereotype.Component;

@Slf4j
@StepScope
@Component
@RequiredArgsConstructor
public class AzureCredentialItemReader implements ItemReader<AzureApiCredentialDto> {

    private final AzureCredentialProperties azureCredentialProperties;

    @Override
    public AzureApiCredentialDto read() {
        AzureApiCredentialDto azureApiCredentialDto = new AzureApiCredentialDto();

        azureApiCredentialDto.setTenantId(azureCredentialProperties.getTenantId());
        azureApiCredentialDto.setClientId(azureCredentialProperties.getClientId());
        azureApiCredentialDto.setClientSecret(azureCredentialProperties.getClientSecret());
        azureApiCredentialDto.setSubscriptionId(azureCredentialProperties.getSubscriptionId());

        log.info("Azure credentials loaded: {} items", azureCredentialProperties.getTenantId());
        return azureApiCredentialDto;
    }
}
