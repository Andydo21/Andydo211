package com.andd.DoDangAn.DoDangAn.config;

import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
public class AppConfig {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        // 1. Tạo HTTP client với HttpClientBuilder
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(
                HttpClientBuilder.create().build()
        );

        // 2. Cấu hình timeout
        factory.setConnectTimeout(Duration.ofMillis(5000));
        factory.setConnectionRequestTimeout(Duration.ofMillis(5000));

        // 3. Xây dựng RestTemplate
        return builder
                .requestFactory(() -> factory)
                .build();
    }
}