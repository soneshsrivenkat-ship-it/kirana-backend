package com.example.demo.dao;

import com.example.demo.external.CurrencyClient;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
@RequiredArgsConstructor
public class CurrencyDao {

    private final CurrencyClient currencyClient;

    @Cacheable(value = "currencyRates",
            key = "#baseCurrency",
            unless = "#result == null")
    public Map<String, Object> getRates(String baseCurrency) {

        return currencyClient.fetchRates(baseCurrency);
    }

    @CacheEvict(value = "currencyRates",
            key = "#baseCurrency")
    public void invalidate(String baseCurrency) {
        // Manual invalidation if needed
    }
}
