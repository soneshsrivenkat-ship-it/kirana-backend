package com.example.demo.dao;

import com.example.demo.external.CurrencyClient;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Repository;

import java.util.Map;

/**
 * CurrencyDao
 *
 * Responsible for interacting with external currency rate provider.
 * This DAO layer wraps CurrencyClient and adds caching behavior
 * using Redis (via Spring Cache abstraction).
 *
 * Caching Strategy:
 * - Currency rates are cached based on baseCurrency.
 * - Cache name: "currencyRates"
 * - Prevents unnecessary external API calls.
 */
@Repository
@RequiredArgsConstructor
public class CurrencyDao {

    /**
     * External client used to fetch live currency rates.
     */
    private final CurrencyClient currencyClient;

    /**
     * Fetch currency exchange rates for a given base currency.
     *
     * This method:
     * - Calls external API via CurrencyClient
     * - Caches the result in Redis
     * - Avoids caching null responses
     *
     * Cache Details:
     * - Cache Name: currencyRates
     * - Key: baseCurrency
     *
     * Example:
     * getRates("INR") → caches under key "INR"
     *
     * @param baseCurrency Base currency code (e.g., INR, USD)
     * @return Map containing exchange rate response
     */
    @Cacheable(
            value = "currencyRates",
            key = "#baseCurrency",
            unless = "#result == null"
    )
    public Map<String, Object> getRates(String baseCurrency) {

        return currencyClient.fetchRates(baseCurrency);
    }

    /**
     * Manually invalidate cached currency rates for a given base currency.
     *
     * Useful when:
     * - Forcing refresh
     * - Admin-triggered updates
     * - Testing scenarios
     *
     * @param baseCurrency Base currency key to remove from cache
     */
    @CacheEvict(
            value = "currencyRates",
            key = "#baseCurrency"
    )
    public void invalidate(String baseCurrency) {
        // Method intentionally empty.
        // Spring handles cache eviction automatically.
    }
}
