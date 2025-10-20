package com.mcmp.assetcollector;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableConfigurationProperties
@ConfigurationPropertiesScan
public class AssetCollectorApplication {

    public static void main(String[] args) {
        SpringApplication.run(AssetCollectorApplication.class, args);
    }

}
