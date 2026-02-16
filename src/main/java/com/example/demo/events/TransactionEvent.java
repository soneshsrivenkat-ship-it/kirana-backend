package com.example.demo.events;

import com.example.demo.entity.TransactionType;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionEvent {

    private String transactionId;
    private String userId;
    private TransactionType transactionType;
    private BigDecimal totalAmount;
    private String currencyType;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime date;



    public TransactionEvent() {
    }

    public TransactionEvent(String transactionId,
                            String userId,
                            TransactionType transactionType,
                            BigDecimal totalAmount,
                            String currencyType,
                            LocalDateTime date) {
        this.transactionId = transactionId;
        this.userId = userId;
        this.transactionType = transactionType;
        this.totalAmount = totalAmount;
        this.currencyType = currencyType;
        this.date = date;
    }


    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public TransactionType getTransactionType() { return transactionType; }
    public void setTransactionType(TransactionType transactionType) { this.transactionType = transactionType; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public String getCurrencyType() { return currencyType; }
    public void setCurrencyType(String currencyType) { this.currencyType = currencyType; }

    public LocalDateTime getDate() { return date; }
    public void setDate(LocalDateTime date) { this.date = date; }
}
