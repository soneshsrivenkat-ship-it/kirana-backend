package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * TransactionItemRequest
 *
 * DTO representing a single product inside a transaction request.
 *
 * Purpose:
 * - Used when creating a new transaction
 * - Specifies product and purchase quantity
 *
 * Business Flow:
 * - Product must exist
 * - Stock must be validated
 * - Quantity reduces stock for CREDIT transactions
 *
 * Used In:
 * POST /transactions
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionItemRequest {

    /**
     * ID of the product being purchased.
     */
    private String productId;

    /**
     * Quantity of the product being purchased.
     */
    private int quantity;
}
