package com.mcmp.cost.ncp.collector.batch.vm;

import com.mcmp.cost.ncp.collector.entity.NcpApiCredential;
import com.mcmp.cost.ncp.collector.entity.NcpCostVmMonth;
import com.mcmp.cost.ncp.collector.service.NcpCostMonthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class NcpCostVmItemProcessor implements ItemProcessor<NcpApiCredential, List<NcpCostVmMonth>> {

    private final NcpCostMonthService ncpCostMonthService;

    @Override
    public List<NcpCostVmMonth> process(NcpApiCredential ncpApiCredential) throws Exception {
        log.info("Processing Ncp cost data for tenant: {}", ncpApiCredential.getId());
        return ncpCostMonthService.getCostByVm(ncpApiCredential);
    }
}
