package com.mcmp.azure.vm.rightsizer.client;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class AlarmServiceConfig {

    @Bean
    RestClient alarmRestClient(RestClient.Builder builder) {
        return builder.baseUrl("http://localhost:9000").build();
    }

    @Bean
    AlarmServiceClient alarmServiceApi(RestClient alarmRestClient) {
        HttpServiceProxyFactory factory =
                HttpServiceProxyFactory.builderFor(RestClientAdapter.create(alarmRestClient))
                        .build();
        return factory.createClient(AlarmServiceClient.class);
    }
}
