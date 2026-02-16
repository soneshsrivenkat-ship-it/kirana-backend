package com.example.demo.service;

import java.math.BigDecimal;

public interface CurrenyService {
    BigDecimal convert(BigDecimal amount,
                       String fromCurrency,
                       String toCurrency);
}
