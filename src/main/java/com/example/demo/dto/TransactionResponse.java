package com.example.demo.dto;

import com.example.demo.entity.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class TransactionResponse implements Serializable {

    private String id;

    private String userId;

    private BigDecimal totalAmount;

    private TransactionType transactionType;

    private String currencyType;

    private LocalDateTime date;

    private List<TransactionItemResponse> items;
    private static final long serialVersionUID = 1L;
}
