package com.mcmp.azure.vm.rightsizer.service;

import com.mcmp.azure.vm.rightsizer.dto.RecommendVmTypeDto;

public interface RecommendVmService {

    RecommendVmTypeDto getRecommendSizeUpVm(String vmId);

    RecommendVmTypeDto getRecommendSizeDownVm(String vmId);

    RecommendVmTypeDto getRecommendModernizeVm(String vmId);
}
