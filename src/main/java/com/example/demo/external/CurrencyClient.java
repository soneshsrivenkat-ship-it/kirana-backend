package com.example.demo.external;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

/**
 * Client responsible for fetching real-time currency exchange rates
 * from an external currency exchange API.
 *
 * This component uses Spring's {@link RestClient} to make HTTP calls
 * to a public exchange rate service.
 */
@Component
@RequiredArgsConstructor
public class CurrencyClient {

    /**
     * Rest client used to perform external HTTP calls.
     */
    private final RestClient restClient;

    /**
     * Fetches exchange rates for the given base currency.
     *
     * Example API:
     * https://api.exchangerate-api.com/v4/latest/{baseCurrency}
     *
     * @param baseCurrency The base currency code (e.g., "INR", "USD").
     * @return A map containing currency codes as keys and their exchange rates as values.
     * @throws RuntimeException if the API response is null or does not contain rate data.
     */
    public Map<String, Object> fetchRates(String baseCurrency) {

        String url =
                "https://api.exchangerate-api.com/v4/latest/" + baseCurrency;

        Map<String, Object> response =
                restClient.get()
                        .uri(url)
                        .retrieve()
                        .body(Map.class);

        if (response == null || !response.containsKey("rates")) {
            throw new RuntimeException("Currency API unavailable");
        }

        return (Map<String, Object>) response.get("rates");
    }
}
