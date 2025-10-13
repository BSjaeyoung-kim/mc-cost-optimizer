package com.mcmp.ncp.vm.rightsizer.service;

import com.mcmp.ncp.vm.rightsizer.dto.AnomalyDto;
import com.mcmp.ncp.vm.rightsizer.dto.NcpCostVmMonthDto;
import java.util.List;

public interface AnomalyCostVmService {

    List<AnomalyDto> getAnomalyCostVm(String region);

    AnomalyDto getAnomalyCostByVmId(NcpCostVmMonthDto ncpCostVmMonthDto);

}
