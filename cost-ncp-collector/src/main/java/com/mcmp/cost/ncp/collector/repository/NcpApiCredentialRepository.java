package com.mcmp.cost.ncp.collector.repository;

import com.mcmp.cost.ncp.collector.entity.NcpApiCredential;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NcpApiCredentialRepository extends JpaRepository<NcpApiCredential, Long> {
}
