package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * TransactionRequest
 *
 * DTO used to create a new transaction.
 *
 * Purpose:
 * - Represents a sales transaction request from client
 * - Contains selected products and currency
 *
 * Business Flow:
 * - Each item will be validated
 * - Stock will be checked
 * - Total amount will be calculated
 * - CREDIT transaction will be created
 *
 * Used In:
 * POST /transactions
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionRequest {

    /**
     * Currency type for the transaction.
     * Example: INR, USD
     */
    private String currencyType;

    /**
     * List of products included in this transaction.
     * Must not be empty.
     */
    private List<TransactionItemRequest> items;
}
