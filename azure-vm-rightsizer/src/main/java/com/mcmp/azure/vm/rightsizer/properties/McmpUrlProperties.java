package com.mcmp.azure.vm.rightsizer.properties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "mcmp.url")
public class McmpUrlProperties {

    private final String alarmService;
}
