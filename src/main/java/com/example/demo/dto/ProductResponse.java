package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class ProductResponse implements Serializable {

    private String id;
    private String name;
    private String category;
    private BigDecimal price;
    private Integer stockQuantity;
    private String unit;
    private Boolean active;
    private Boolean refundable;
    private static final long serialVersionUID = 1L;
}
