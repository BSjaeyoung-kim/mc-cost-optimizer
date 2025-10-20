package com.mcmp.assetcollector.client;

import com.mcmp.assetcollector.properties.AssetCollectUrlProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
@RequiredArgsConstructor
public class AssetMetricCollectConfig {

    private final AssetCollectUrlProperties assetCollectUrlProperties;

    @Bean
    RestClient assetMetricRestClient(RestClient.Builder builder) {
        return builder.baseUrl(assetCollectUrlProperties.getUrl()).build();
    }

    @Bean
    AssetMetricCollectClient alarmServiceApi(RestClient assetMetricRestClient) {
        HttpServiceProxyFactory factory =
                HttpServiceProxyFactory.builderFor(RestClientAdapter.create(assetMetricRestClient))
                        .build();
        return factory.createClient(AssetMetricCollectClient.class);
    }
}
