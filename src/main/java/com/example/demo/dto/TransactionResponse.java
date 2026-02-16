package com.example.demo.dto;

import com.example.demo.entity.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * TransactionResponse
 *
 * DTO returned to the client after:
 * - Creating a transaction
 * - Fetching transaction by ID
 * - Fetching transaction list
 * - Processing a refund
 *
 * Purpose:
 * - Represents a complete transaction record
 * - Includes transaction metadata + items
 * - Supports both CREDIT (sales) and DEBIT (refund) flows
 *
 * Business Meaning:
 * CREDIT → Normal sale
 * DEBIT  → Refund transaction
 *
 * parentTransactionId:
 * - NULL  → Normal sale transaction
 * - NOT NULL → Refund transaction referencing original sale
 *
 * Serializable:
 * - Required for Redis caching
 * - Required for distributed environments
 */
@Data
@AllArgsConstructor
public class TransactionResponse implements Serializable {

    /**
     * Unique transaction ID (UUID).
     */
    private String id;

    /**
     * User who performed the transaction.
     */
    private String userId;

    /**
     * Total monetary value of the transaction.
     * For refund, represents total refunded amount.
     */
    private BigDecimal totalAmount;

    /**
     * Type of transaction:
     * - CREDIT → Sale
     * - DEBIT  → Refund
     */
    private TransactionType transactionType;

    /**
     * Currency used for the transaction.
     * Example: INR, USD
     */
    private String currencyType;

    /**
     * Reference to original transaction ID (for refunds).
     * NULL for normal transactions.
     */
    private String parentTransactionId;

    /**
     * Timestamp when transaction was created.
     */
    private LocalDateTime date;

    /**
     * List of items included in this transaction.
     */
    private List<TransactionItemResponse> items;

    private static final long serialVersionUID = 1L;
}
