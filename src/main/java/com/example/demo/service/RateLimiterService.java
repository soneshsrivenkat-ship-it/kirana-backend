package com.example.demo.service;

import com.example.demo.config.RateLimitProperties;
import io.github.bucket4j.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * RateLimiterService
 *
 * Responsible for:
 *  - Resolving per-user buckets
 *  - Applying role-based rate limit configuration
 *  - Creating buckets dynamically when not present
 *
 * Uses in-memory ConcurrentHashMap for bucket storage.
 */
@Service
public class RateLimiterService {

    private static final Logger log =
            LoggerFactory.getLogger(RateLimiterService.class);

    private final RateLimitProperties properties;

    /**
     * In-memory bucket cache.
     * Key format: apiName:userKey
     */
    private final Map<String, Bucket> bucketCache =
            new ConcurrentHashMap<>();

    public RateLimiterService(RateLimitProperties properties) {
        this.properties = properties;
    }

    /**
     * Resolves or creates a Bucket for a specific API and user.
     *
     * @param apiName API identifier (transaction, product, etc.)
     * @param userKey userId or IP address
     * @param role    user role (ADMIN, STAFF, USER)
     * @return Bucket configured with appropriate limits
     */
    public Bucket resolveBucket(String apiName,
                                String userKey,
                                String role) {

        String bucketKey = apiName + ":" + userKey;

        return bucketCache.computeIfAbsent(bucketKey, key -> {

            log.debug("Creating new rate limit bucket for key={}", bucketKey);

            Map<String, Map<String, RateLimitProperties.ApiLimit>> limits =
                    properties.getLimits();

            if (limits == null || !limits.containsKey(apiName)) {
                log.warn("No rate limit configuration found for api={}", apiName);
                throw new RuntimeException("Rate limit config missing for API: " + apiName);
            }

            RateLimitProperties.ApiLimit config =
                    limits.get(apiName)
                            .getOrDefault(role,
                                    limits.get(apiName).get("USER"));

            log.info("Applying rate limit: api={}, role={}, capacity={}, duration={}s",
                    apiName,
                    role,
                    config.getCapacity(),
                    config.getDuration());

            Bandwidth limit = Bandwidth.classic(
                    config.getCapacity(),
                    Refill.intervally(
                            config.getCapacity(),
                            Duration.ofSeconds(config.getDuration())
                    )
            );

            return Bucket.builder()
                    .addLimit(limit)
                    .build();
        });
    }
}
