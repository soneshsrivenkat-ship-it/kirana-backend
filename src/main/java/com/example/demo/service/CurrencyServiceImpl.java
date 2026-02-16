package com.example.demo.service;

import com.example.demo.dao.CurrencyDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CurrencyServiceImpl implements CurrenyService {

    private final CurrencyDao currencyDao;

    @Override
    public BigDecimal convert(BigDecimal amount,
                              String fromCurrency,
                              String toCurrency) {

        if (amount == null) {
            return BigDecimal.ZERO;
        }

        fromCurrency = fromCurrency.toUpperCase();
        toCurrency = toCurrency.toUpperCase();

        if (fromCurrency.equals(toCurrency)) {
            return amount.setScale(2, RoundingMode.HALF_UP);
        }

        Map<String, Object> rates =
                currencyDao.getRates(fromCurrency);

        if (!rates.containsKey(toCurrency)) {
            throw new RuntimeException("Currency not supported");
        }

        BigDecimal rate =
                new BigDecimal(rates.get(toCurrency).toString());

        return amount.multiply(rate)
                .setScale(2, RoundingMode.HALF_UP);
    }
}
