package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class SalesSummaryResponse {

    private String currency;

    private BigDecimal netSales;

    private BigDecimal totalCreditSales;
    private BigDecimal totalDebitSales;

    private Long totalTransactions;
    private Long totalCreditTransactions;
    private Long totalDebitTransactions;
}
