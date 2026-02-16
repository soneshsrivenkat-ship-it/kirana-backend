package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {

    private String name;
    private String category;
    private BigDecimal price;
    private Integer stockQuantity;
    private String unit;
    private Boolean active;
    private Boolean refundable;
}
