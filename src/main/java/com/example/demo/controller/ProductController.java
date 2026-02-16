package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Product Controller
 *
 * This controller manages all product-related operations.
 *
 * Responsibilities:
 * - Create new product
 * - Update existing product
 * - Fetch all products
 * - Fetch product by ID
 *
 * Base URL: /products
 */
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    // Service layer responsible for business logic
    private final ProductService productService;

    /**
     * Creates a new product.
     *
     * Endpoint: POST /products
     *
     * Accepts product details such as:
     * - Name
     * - Price
     * - Stock Quantity
     * - Currency
     *
     * @param request Contains product creation details
     * @return ApiResponse containing created ProductResponse
     */
    @PostMapping
    public ApiResponse<ProductResponse> add(
            @RequestBody ProductRequest request) {

        ProductResponse response =
                productService.createProduct(request);

        return new ApiResponse<>(true, response);
    }

    /**
     * Updates an existing product.
     *
     * Endpoint: PUT /products/{id}
     *
     * Allows updating:
     * - Product name
     * - Price
     * - Stock quantity
     * - Other editable fields
     *
     * @param id Product ID to update
     * @param request Updated product details
     * @return ApiResponse containing updated ProductResponse
     */
    @PutMapping("/{id}")
    public ApiResponse<ProductResponse> update(
            @PathVariable String id,
            @RequestBody ProductRequest request) {

        ProductResponse response =
                productService.updateProduct(id, request);

        return new ApiResponse<>(true, response);
    }

    /**
     * Retrieves all available products.
     *
     * Endpoint: GET /products
     *
     * Used for:
     * - Product listing
     * - Inventory display
     * - Admin dashboard
     *
     * @return ApiResponse containing list of ProductResponse
     */
    @GetMapping
    public ApiResponse<List<ProductResponse>> all() {

        return new ApiResponse<>(
                true,
                productService.getAllProducts()
        );
    }

    /**
     * Retrieves a single product by ID.
     *
     * Endpoint: GET /products/{id}
     *
     * @param id Product ID
     * @return ApiResponse containing ProductResponse
     */
    @GetMapping("/{id}")
    public ApiResponse<ProductResponse> getById(
            @PathVariable String id) {

        return new ApiResponse<>(
                true,
                productService.getProductById(id)
        );
    }
}
