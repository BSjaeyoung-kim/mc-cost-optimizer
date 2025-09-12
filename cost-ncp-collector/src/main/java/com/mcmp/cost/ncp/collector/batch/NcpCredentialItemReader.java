package com.mcmp.cost.ncp.collector.batch;

import com.mcmp.cost.ncp.collector.entity.NcpApiCredential;
import com.mcmp.cost.ncp.collector.repository.NcpApiCredentialRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemReader;
import org.springframework.stereotype.Component;
import java.util.Iterator;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class NcpCredentialItemReader implements ItemReader<NcpApiCredential> {

    private final NcpApiCredentialRepository ncpApiCredentialRepository;
    private Iterator<NcpApiCredential> credentialIterator;

    @Override
    public NcpApiCredential read() {
        if (credentialIterator == null) {
            List<NcpApiCredential> credentials = ncpApiCredentialRepository.findAll();
            credentialIterator = credentials.iterator();
            log.info("Ncp credentials loaded: {} items", credentials.size());
        }

        if (credentialIterator.hasNext()) {
            NcpApiCredential credential = credentialIterator.next();
            log.debug("Reading credential for tenant: {}", credential.getId());
            return credential;
        }

        return null; // 더 이상 읽을 데이터가 없음
    }
}
