package com.example.demo.external;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class CurrencyClient {

    private final RestClient restClient;

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
