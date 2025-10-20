package com.mcmp.assetcollector.properties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "asset.collect")
public class AssetCollectUrlProperties {

    private final String url;
}
