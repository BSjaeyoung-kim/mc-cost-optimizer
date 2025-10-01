package com.mcmp.azure.vm.rightsizer.service;

import com.mcmp.azure.vm.rightsizer.dto.AnomalyDto;
import com.mcmp.azure.vm.rightsizer.dto.AzureCostVmDailyDto;
import java.util.List;

public interface AnomalyCostVmService {

    List<AnomalyDto> getAnomalyCostVm();

    AnomalyDto getAnomalyCostByVmId(AzureCostVmDailyDto azureCostVmDailyDto);

}
