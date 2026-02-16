package com.example.demo.service;

import com.example.demo.config.RateLimitProperties;
import io.github.bucket4j.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimiterService {

    private static final Logger log =
            LoggerFactory.getLogger(RateLimiterService.class);

    private final RateLimitProperties properties;

    private final Map<String, Bucket> bucketCache =
            new ConcurrentHashMap<>();

    public RateLimiterService(RateLimitProperties properties) {
        this.properties = properties;
    }

    public Bucket resolveBucket(String apiName,
                                String userKey,
                                String role) {

        String bucketKey = apiName + ":" + userKey;

        return bucketCache.computeIfAbsent(bucketKey, key -> {

            RateLimitProperties.ApiLimit config =
                    properties.getLimits()
                            .get(apiName)
                            .getOrDefault(role,
                                    properties.getLimits()
                                            .get(apiName)
                                            .get("USER"));

            log.info("Creating bucket for {}", bucketKey);

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
