package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * TransactionItemResponse
 *
 * DTO representing a product inside a transaction response.
 *
 * Purpose:
 * - Sent back to client after transaction creation
 * - Used in transaction details API
 * - Used in refund responses
 *
 * Business Meaning:
 * - priceAtPurchase ensures price consistency
 * - refundable determines refund eligibility
 * - itemTotal = priceAtPurchase × purchaseQuantity
 */
@Data
@AllArgsConstructor
public class TransactionItemResponse implements Serializable {

    /**
     * Unique identifier of the transaction item.
     */
    private String id;

    /**
     * ID of the parent transaction.
     */
    private String transactionId;

    /**
     * Product ID.
     */
    private String productId;

    /**
     * Product name at time of purchase.
     */
    private String productName;

    /**
     * Quantity purchased.
     */
    private Integer purchaseQuantity;

    /**
     * Price of the product at time of transaction.
     */
    private BigDecimal priceAtPurchase;

    /**
     * Indicates if this item can be refunded.
     */
    private Boolean refundable;

    /**
     * Total cost of this item.
     * itemTotal = priceAtPurchase × purchaseQuantity
     */
    private BigDecimal itemTotal;

    private static final long serialVersionUID = 1L;
}
