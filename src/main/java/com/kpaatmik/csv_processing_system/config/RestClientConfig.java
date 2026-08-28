package com.kpaatmik.csv_processing_system.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient zipCodeRestClient() {
        return RestClient.builder()
                .baseUrl("https://api.zippopotam.us/")
                .build();
    }
}