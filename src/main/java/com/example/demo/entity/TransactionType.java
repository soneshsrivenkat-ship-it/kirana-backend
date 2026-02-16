package com.example.demo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;


@AllArgsConstructor
public enum TransactionType {
    DEBIT,   // Sale
    CREDIT   // Refund
}
