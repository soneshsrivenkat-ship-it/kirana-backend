package com.example.demo.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.*;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Redis configuration class responsible for:
 *
 * <ul>
 *     <li>Enabling Spring Cache abstraction</li>
 *     <li>Configuring Redis as the cache provider</li>
 *     <li>Setting custom serialization for complex objects</li>
 *     <li>Defining TTL (Time-To-Live) per cache region</li>
 * </ul>
 *
 * <p>This configuration ensures:
 * <ul>
 *     <li>Proper serialization of Java 8 Date/Time (LocalDateTime)</li>
 *     <li>Enum serialization support</li>
 *     <li>Different TTL policies per business domain</li>
 * </ul>
 *
 * <p>Example Use Cases:
 * <ul>
 *     <li>Product listing caching</li>
 *     <li>Transaction history caching</li>
 *     <li>Refund responses caching</li>
 *     <li>Currency exchange rate caching</li>
 * </ul>
 *
 * <p>Redis is used here to improve performance,
 * reduce database load, and enable distributed caching
 * across multiple application instances.
 */
@Configuration
@EnableCaching
public class RedisConfig {

    /**
     * Creates and configures the Redis CacheManager bean.
     *
     * <p>This method:
     * <ul>
     *     <li>Configures JSON serialization using Jackson</li>
     *     <li>Handles Java 8 date/time serialization</li>
     *     <li>Sets default cache TTL</li>
     *     <li>Overrides TTL per specific cache name</li>
     * </ul>
     *
     * @param connectionFactory Redis connection factory provided by Spring
     * @return Configured CacheManager backed by Redis
     */
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {


        /**
         * ObjectMapper configured to:
         * - Support Java 8 time classes (LocalDateTime)
         * - Store dates as ISO format instead of timestamps
         */
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        /**
         * Generic serializer that converts objects into JSON
         * before storing in Redis.
         */
        GenericJackson2JsonRedisSerializer serializer =
                new GenericJackson2JsonRedisSerializer(objectMapper);



        /**
         * Default cache behavior:
         * - TTL: 5 minutes
         * - Null values are not cached
         * - Keys stored as Strings
         * - Values stored as JSON
         */
        RedisCacheConfiguration defaultConfig =
                RedisCacheConfiguration.defaultCacheConfig()
                        .entryTtl(Duration.ofMinutes(5))
                        .disableCachingNullValues()
                        .serializeKeysWith(
                                RedisSerializationContext.SerializationPair
                                        .fromSerializer(new StringRedisSerializer()))
                        .serializeValuesWith(
                                RedisSerializationContext.SerializationPair
                                        .fromSerializer(serializer));



        /**
         * Individual cache regions with specific TTL policies
         * depending on business requirements.
         */
        Map<String, RedisCacheConfiguration> cacheConfigurations =
                new HashMap<>();


        cacheConfigurations.put("products",
                defaultConfig.entryTtl(Duration.ofMinutes(10)));


        cacheConfigurations.put("transactions",
                defaultConfig.entryTtl(Duration.ofMinutes(5)));

        cacheConfigurations.put("refunds",
                defaultConfig.entryTtl(Duration.ofMinutes(3)));

        cacheConfigurations.put("summary",
                defaultConfig.entryTtl(Duration.ofMinutes(2)));

        cacheConfigurations.put("currencyRates",
                defaultConfig.entryTtl(Duration.ofHours(1)));



        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigurations)
                .build();
    }
}
