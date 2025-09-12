package com.mcmp.cost.ncp.collector.repository;

import com.mcmp.cost.ncp.collector.entity.NcpCostVmMonth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NcpCostVmMonthRepository extends JpaRepository<NcpCostVmMonth, Long> {
}
