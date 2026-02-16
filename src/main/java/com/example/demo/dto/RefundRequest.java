package com.example.demo.dto;

import lombok.Data;

import java.util.List;

@Data
public class RefundRequest {

    private String transactionId;

    private List<String> productIds;
}

