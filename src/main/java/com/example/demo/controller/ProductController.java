package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.dto.*;
import com.example.demo.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    /**
     * Create Product
     */
    @PostMapping
    public ApiResponse<ProductResponse> add(
            @RequestBody ProductRequest request) {

        ProductResponse response =
                productService.createProduct(request);

        return new ApiResponse<>(true, response);
    }

    /**
     * Update Product
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
     * Get All Products
     */
    @GetMapping
    public ApiResponse<List<ProductResponse>> all() {

        return new ApiResponse<>(
                true,
                productService.getAllProducts()
        );
    }

    /**
     * Get Product By Id
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
