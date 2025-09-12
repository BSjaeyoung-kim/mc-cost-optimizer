package com.mcmp.cost.azure.collector.batch;

import com.mcmp.cost.azure.collector.entity.AzureApiCredential;
import com.mcmp.cost.azure.collector.repository.AzureApiCredentialRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemReader;
import org.springframework.stereotype.Component;
import java.util.Iterator;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AzureCredentialItemReader implements ItemReader<AzureApiCredential> {

    private final AzureApiCredentialRepository azureApiCredentialRepository;
    private Iterator<AzureApiCredential> credentialIterator;

    @Override
    public AzureApiCredential read() {
        if (credentialIterator == null) {
            List<AzureApiCredential> credentials = azureApiCredentialRepository.findAll();
            credentialIterator = credentials.iterator();
            log.info("Azure credentials loaded: {} items", credentials.size());
        }

        if (credentialIterator.hasNext()) {
            AzureApiCredential credential = credentialIterator.next();
            log.debug("Reading credential for tenant: {}", credential.getTenantId());
            return credential;
        }

        return null; // 더 이상 읽을 데이터가 없음
    }
}
