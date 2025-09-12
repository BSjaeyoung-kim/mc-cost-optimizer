package com.mcmp.cost.azure.collector.repository;

import com.mcmp.cost.azure.collector.entity.AzureCostVmDaily;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AzureCostVmDailyRepository extends JpaRepository<AzureCostVmDaily, Long> {
}
