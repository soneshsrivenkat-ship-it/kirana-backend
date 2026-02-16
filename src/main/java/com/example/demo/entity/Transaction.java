package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;


    @Column(name = "parent_transaction_id")
    private String parentTransactionId;

    private String userId;

    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    private String currencyType;

    @CreationTimestamp
    private LocalDateTime date;
}
