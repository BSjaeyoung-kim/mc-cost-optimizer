package com.mcmp.cost.ncp.collector.controller;

import com.mcmp.cost.ncp.collector.dto.NcpApiCredentialDto;
import com.mcmp.cost.ncp.collector.properties.NcpCredentialProperties;
import com.mcmp.cost.ncp.collector.service.NcpCostMonthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api")
public class HelloController {

    private final NcpCostMonthService ncpCostMonthService;
    private final NcpCredentialProperties ncpCredentialProperties;

    @GetMapping(value = "/ncp/cost/test")
    public ResponseEntity<String> test1() {
        NcpApiCredentialDto ncpApiCredentialDto = new NcpApiCredentialDto();
        ncpApiCredentialDto.setIamAccessKey(ncpCredentialProperties.getIamAccessKey());
        ncpApiCredentialDto.setIamSecretKey(ncpCredentialProperties.getIamSecretKey());
        ncpCostMonthService.getCostByService(ncpApiCredentialDto);
        return ResponseEntity.ok("Test Cost.");
    }
}
