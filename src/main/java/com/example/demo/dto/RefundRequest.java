package com.example.demo.dto;

import lombok.Data;

import java.util.List;

/**
 * RefundRequest
 *
 * DTO used to initiate a refund operation.
 *
 * Purpose:
 * - Links a refund to an existing CREDIT transaction
 * - Supports partial refunds by allowing selection of specific product IDs
 *
 * Business Logic:
 * - transactionId → Original CREDIT transaction ID
 * - productIds → List of products to refund (partial refund supported)
 * - A new DEBIT transaction will be created
 * - parentTransactionId will reference the original transaction
 *
 * Used In:
 * POST /refund
 */
@Data
public class RefundRequest {

    /**
     * ID of the original CREDIT transaction
     * from which refund is requested.
     */
    private String transactionId;

    /**
     * List of product IDs to be refunded.
     * Supports partial refunds.
     */
    private List<String> productIds;
}
