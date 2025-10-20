package com.mcmp.assetcollector.client;

import com.mcmp.assetcollector.properties.CostOptiBeUrlProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
@RequiredArgsConstructor
public class CostOptiConfig {

    private final CostOptiBeUrlProperties costOptiBeUrlProperties;

    @Bean
    RestClient costOptiRestClient(RestClient.Builder builder) {
        return builder.baseUrl(costOptiBeUrlProperties.getUrl()).build();
    }

    @Bean
    CostOptiClient costOptiServiceApi(RestClient costOptiRestClient) {
        HttpServiceProxyFactory factory =
                HttpServiceProxyFactory.builderFor(RestClientAdapter.create(costOptiRestClient))
                        .build();
        return factory.createClient(CostOptiClient.class);
    }
}
