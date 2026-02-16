package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "transaction_items")
public class TransactionItem {

    @Id
    private String id;

    @Column(name = "transaction_id")
    private String transactionId;

    private String productId;

    private String productName;

    private Integer purchaseQuantity;

    private BigDecimal priceAtPurchase;

    private Boolean refundable;

    private BigDecimal itemTotal;
}
