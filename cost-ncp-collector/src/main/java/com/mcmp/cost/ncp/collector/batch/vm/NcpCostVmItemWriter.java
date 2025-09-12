package com.mcmp.cost.ncp.collector.batch.vm;

import com.mcmp.cost.ncp.collector.entity.NcpCostServiceMonth;
import com.mcmp.cost.ncp.collector.entity.NcpCostVmMonth;
import com.mcmp.cost.ncp.collector.repository.NcpCostServiceMonthRepository;
import com.mcmp.cost.ncp.collector.repository.NcpCostVmMonthRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class NcpCostVmItemWriter implements ItemWriter<List<NcpCostVmMonth>> {

    private final NcpCostVmMonthRepository ncpCostVmMonthRepository;

    @Override
    public void write(Chunk<? extends List<NcpCostVmMonth>> chunk) throws Exception {
        for (List<NcpCostVmMonth> ncpCostVmMonthList : chunk) {
            if (ncpCostVmMonthList != null && !ncpCostVmMonthList.isEmpty()) {
                ncpCostVmMonthRepository.saveAll(ncpCostVmMonthList);
                log.info("Saved {} Ncp cost service records to database", ncpCostVmMonthList.size());
            }
        }
    }
}
