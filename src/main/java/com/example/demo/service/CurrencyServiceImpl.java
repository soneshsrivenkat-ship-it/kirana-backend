package com.example.demo.service;

import com.example.demo.dao.CurrencyDao;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

/**
 * Currency Service Implementation.
 *
 * Handles currency conversion using external exchange rates.
 *
 * Responsibilities:
 *  - Fetch exchange rates (cached via CurrencyDao)
 *  - Perform safe currency conversion
 *  - Apply rounding rules
 */
@Service
@RequiredArgsConstructor
public class CurrencyServiceImpl implements CurrenyService {

    private static final Logger log =
            LoggerFactory.getLogger(CurrencyServiceImpl.class);

    private final CurrencyDao currencyDao;

    /**
     * Convert amount from one currency to another.
     *
     * @param amount        Amount to convert
     * @param fromCurrency  Source currency
     * @param toCurrency    Target currency
     * @return Converted amount rounded to 2 decimal places
     */
    @Override
    public BigDecimal convert(BigDecimal amount,
                              String fromCurrency,
                              String toCurrency) {

        if (amount == null) {
            log.warn("Conversion requested with null amount");
            return BigDecimal.ZERO;
        }

        fromCurrency = fromCurrency.toUpperCase();
        toCurrency = toCurrency.toUpperCase();

        log.info("Currency conversion requested: {} {} -> {}",
                amount, fromCurrency, toCurrency);

        if (fromCurrency.equals(toCurrency)) {
            log.debug("Same currency conversion, returning original amount");
            return amount.setScale(2, RoundingMode.HALF_UP);
        }

        Map<String, Object> rates =
                currencyDao.getRates(fromCurrency);

        if (!rates.containsKey(toCurrency)) {
            log.error("Unsupported currency conversion: {} -> {}",
                    fromCurrency, toCurrency);
            throw new RuntimeException("Currency not supported");
        }

        BigDecimal rate =
                new BigDecimal(rates.get(toCurrency).toString());

        BigDecimal convertedAmount =
                amount.multiply(rate)
                        .setScale(2, RoundingMode.HALF_UP);

        log.info("Conversion successful: {} {} = {} {} (rate: {})",
                amount, fromCurrency,
                convertedAmount, toCurrency,
                rate);

        return convertedAmount;
    }
}
