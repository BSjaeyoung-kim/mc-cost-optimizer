package com.mcmp.cost.azure.collector;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class CostAzureCollectorApplication {

    public static void main(String[] args) {
        SpringApplication.run(CostAzureCollectorApplication.class, args);
    }

}
