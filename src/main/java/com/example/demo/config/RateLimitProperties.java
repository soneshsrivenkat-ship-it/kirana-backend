package com.example.demo.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * Configuration class responsible for binding rate limiting
 * properties from application.yml or application.properties.
 *
 * <p>This class maps configuration defined under:
 *
 * <pre>
 * rate-limit:
 *   limits:
 *     refund:
 *       USER:
 *         capacity: 5
 *         duration: 60
 * </pre>
 *
 * <p>Structure Explanation:
 * <ul>
 *     <li>First Key  → API Name (e.g., refund, transaction, product)</li>
 *     <li>Second Key → Role/UserType (e.g., USER, ADMIN)</li>
 *     <li>ApiLimit   → Contains bucket capacity and refill duration</li>
 * </ul>
 *
 * <p>This configuration is used by {@code RateLimiterService}
 * to dynamically create token buckets per API and per user type.
 *
 * <p>Example Use Case:
 * <ul>
 *     <li>Limit refund API to 5 requests per 60 seconds per user</li>
 *     <li>Limit transaction creation to 10 requests per 1 minute</li>
 * </ul>
 *
 * <p>This class is automatically loaded by Spring Boot
 * using @ConfigurationProperties.
 */
@Configuration
@ConfigurationProperties(prefix = "rate-limit")
@Getter
@Setter
public class RateLimitProperties {

    /**
     * Nested Map structure storing API rate limits.
     *
     * <p>Structure:
     * Map<
     *     API_NAME,
     *     Map<
     *         USER_ROLE,
     *         ApiLimit
     *     >
     * >
     *
     * <p>Example:
     * limits.get("refund").get("USER").getCapacity()
     */
    private Map<String, Map<String, ApiLimit>> limits;

    /**
     * Inner class representing individual API rate limit configuration.
     *
     * <p>capacity → Maximum number of allowed requests
     * <p>duration → Time window in seconds for capacity reset
     *
     * <p>This is used to configure token bucket limits dynamically.
     */
    @Getter
    @Setter
    public static class ApiLimit {

        /**
         * Maximum number of requests allowed within the duration window.
         */
        private long capacity;

        /**
         * Duration in seconds for which the capacity is valid.
         * After this duration, tokens are refilled.
         */
        private long duration;
    }
}
