package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

/**
 * SalesSummaryResponse
 *
 * DTO representing aggregated sales metrics.
 *
 * Purpose:
 * - Provides summarized financial statistics
 * - Used for dashboard reporting
 * - Supports multi-currency reporting
 *
 * Business Meaning:
 * - CREDIT → Sales (money coming in)
 * - DEBIT → Refunds (money going out)
 * - netSales = totalCreditSales - totalDebitSales
 *
 * Used In:
 * GET /sales/summary
 */
@Data
@AllArgsConstructor
public class SalesSummaryResponse {

    /**
     * Currency in which the summary is calculated.
     * Example: INR, USD
     */
    private String currency;

    /**
     * Net sales after deducting refunds.
     * netSales = credits - debits
     */
    private BigDecimal netSales;

    /**
     * Total amount of CREDIT transactions.
     */
    private BigDecimal totalCreditSales;

    /**
     * Total amount of DEBIT transactions (refunds).
     */
    private BigDecimal totalDebitSales;

    /**
     * Total number of transactions (credit + debit).
     */
    private Long totalTransactions;

    /**
     * Total number of CREDIT transactions.
     */
    private Long totalCreditTransactions;

    /**
     * Total number of DEBIT transactions.
     */
    private Long totalDebitTransactions;
}
