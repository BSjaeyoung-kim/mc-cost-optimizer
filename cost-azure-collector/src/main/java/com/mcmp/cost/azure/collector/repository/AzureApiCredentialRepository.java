package com.mcmp.cost.azure.collector.repository;

import com.mcmp.cost.azure.collector.entity.AzureApiCredential;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AzureApiCredentialRepository extends JpaRepository<AzureApiCredential, Long> {
}
