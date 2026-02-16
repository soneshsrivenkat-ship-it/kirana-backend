package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * ProductRequest
 *
 * DTO used to create or update a product.
 *
 * Purpose:
 * - Accepts product details from client
 * - Used in ProductController for create and update operations
 *
 * Used In:
 * POST /products
 * PUT  /products/{id}
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {

    /**
     * Name of the product.
     */
    private String name;

    /**
     * Category of the product.
     * Example: Grocery, Dairy, Cleaning, Snacks
     */
    private String category;

    /**
     * Selling price of the product.
     */
    private BigDecimal price;

    /**
     * Available stock quantity.
     */
    private Integer stockQuantity;

    /**
     * Unit of measurement.
     * Example: kg, liter, piece, pack
     */
    private String unit;

    /**
     * Indicates whether product is active and available for sale.
     */
    private Boolean active;

    /**
     * Indicates whether product is eligible for refund.
     */
    private Boolean refundable;
}
