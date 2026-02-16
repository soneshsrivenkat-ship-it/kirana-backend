package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/**
 * Configuration class responsible for creating and exposing
 * a RestClient bean in the Spring application context.
 *
 * RestClient is used to make HTTP calls to external services
 * such as currency APIs, third-party integrations, or microservices.
 *
 * By defining it as a Bean:
 * - It can be injected anywhere using constructor injection
 * - It becomes reusable across the application
 * - It keeps configuration centralized
 */
@Configuration
public class RestClientConfig {

    /**
     * Creates a RestClient instance.
     *
     * This client can be used to:
     * - Call external REST APIs
     * - Fetch data from third-party services
     * - Communicate between microservices
     *
     * @return a configured RestClient instance
     */
    @Bean
    public RestClient restClient() {
        return RestClient.create();
    }
}
