package com.mcmp.cost.ncp.collector.batch.service;

import com.mcmp.cost.ncp.collector.entity.NcpApiCredential;
import com.mcmp.cost.ncp.collector.entity.NcpCostServiceMonth;
import com.mcmp.cost.ncp.collector.service.NcpCostMonthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class NcpCostServiceItemProcessor implements ItemProcessor<NcpApiCredential, List<NcpCostServiceMonth>> {

    private final NcpCostMonthService ncpCostMonthService;

    @Override
    public List<NcpCostServiceMonth> process(NcpApiCredential ncpApiCredential) throws Exception {
        log.info("Processing Ncp cost data for tenant: {}", ncpApiCredential.getId());
        return ncpCostMonthService.getCostByService(ncpApiCredential);
    }
}
