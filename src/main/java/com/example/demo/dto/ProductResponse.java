package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * ProductResponse
 *
 * DTO returned to clients when fetching product details.
 *
 * Purpose:
 * - Represents product information exposed via API
 * - Used in ProductController responses
 * - Serializable for Redis caching support
 *
 * Used In:
 * GET  /products
 * GET  /products/{id}
 * POST /products
 * PUT  /products/{id}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponse implements Serializable {

    /**
     * Unique identifier of the product.
     */
    private String id;

    /**
     * Name of the product.
     */
    private String name;

    /**
     * Category of the product.
     */
    private String category;

    /**
     * Current selling price.
     */
    private BigDecimal price;

    /**
     * Available stock quantity.
     */
    private Integer stockQuantity;

    /**
     * Unit of measurement (kg, liter, piece, etc.).
     */
    private String unit;

    /**
     * Indicates whether the product is active and available.
     */
    private Boolean active;

    /**
     * Indicates whether the product is refundable.
     */
    private Boolean refundable;

    /**
     * Serial version UID for serialization compatibility.
     */
    private static final long serialVersionUID = 1L;
}
