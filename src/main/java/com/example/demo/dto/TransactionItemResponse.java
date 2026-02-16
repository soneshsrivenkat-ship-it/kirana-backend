package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class TransactionItemResponse implements Serializable {

    private String id;

    private String transactionId;

    private String productId;

    private String productName;

    private Integer purchaseQuantity;

    private BigDecimal priceAtPurchase;

    private Boolean refundable;
    private static final long serialVersionUID = 1L;

    private BigDecimal itemTotal;
}
