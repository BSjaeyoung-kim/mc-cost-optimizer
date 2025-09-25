package com.mcmp.azure.vm.rightsizer.client;

import com.mcmp.azure.vm.rightsizer.properties.McmpUrlProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
@RequiredArgsConstructor
public class AlarmServiceConfig {

    private final McmpUrlProperties mcmpUrlProperties;

    @Bean
    RestClient alarmRestClient(RestClient.Builder builder) {
        return builder.baseUrl(mcmpUrlProperties.getAlarmService()).build();
    }

    @Bean
    AlarmServiceClient alarmServiceApi(RestClient alarmRestClient) {
        HttpServiceProxyFactory factory =
                HttpServiceProxyFactory.builderFor(RestClientAdapter.create(alarmRestClient))
                        .build();
        return factory.createClient(AlarmServiceClient.class);
    }
}
