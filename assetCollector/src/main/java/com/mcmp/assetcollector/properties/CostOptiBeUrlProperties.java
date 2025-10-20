package com.mcmp.assetcollector.properties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "costopti.be")
public class CostOptiBeUrlProperties {

    private final String url;
}
