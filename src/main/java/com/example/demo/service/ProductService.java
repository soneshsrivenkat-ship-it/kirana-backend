package com.example.demo.service;

import com.example.demo.dto.ProductRequest;
import com.example.demo.dto.ProductResponse;
import com.example.demo.entity.Product;
import com.example.demo.exception.ProductNotFoundException;
import com.example.demo.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Product Service.
 *
 * Handles product creation, update, and retrieval logic.
 */
@Service
@RequiredArgsConstructor
public class ProductService {

    private static final Logger log =
            LoggerFactory.getLogger(ProductService.class);

    private final ProductRepository productRepo;

    /**
     * Creates a new product in the system.
     */
    @CacheEvict(value = "products", allEntries = true)
    public ProductResponse createProduct(ProductRequest request) {

        log.info("Creating new product: name={}, category={}",
                request.getName(), request.getCategory());

        Product product = new Product();
        product.setId(UUID.randomUUID().toString());
        product.setName(request.getName());
        product.setCategory(request.getCategory());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());
        product.setUnit(request.getUnit());
        product.setActive(request.getActive());
        product.setRefundable(request.getRefundable());

        productRepo.save(product);

        log.info("Product created successfully with id={}", product.getId());

        return mapToResponse(product);
    }

    /**
     * Updates an existing product.
     */
    @CacheEvict(value = "products", allEntries = true)
    public ProductResponse updateProduct(String id, ProductRequest request) {

        log.info("Updating product with id={}", id);

        Product product = productRepo.findById(id)
                .orElseThrow(() -> {
                    log.warn("Product update failed. Product not found: {}", id);
                    return new ProductNotFoundException("Product not found");
                });

        product.setName(request.getName());
        product.setCategory(request.getCategory());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());
        product.setUnit(request.getUnit());
        product.setActive(request.getActive());
        product.setRefundable(request.getRefundable());

        productRepo.save(product);

        log.info("Product updated successfully: id={}", id);

        return mapToResponse(product);
    }

    /**
     * Retrieves all products.
     */
    @Cacheable(value = "products", key = "'all'")
    public List<ProductResponse> getAllProducts() {

        log.debug("Fetching all products");

        List<ProductResponse> products = productRepo.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        log.debug("Total products fetched: {}", products.size());

        return products;
    }

    /**
     * Retrieves product by ID.
     */
    @Cacheable(value = "products", key = "#id")
    public ProductResponse getProductById(String id) {

        log.debug("Fetching product by id={}", id);

        Product product = productRepo.findById(id)
                .orElseThrow(() -> {
                    log.warn("Product not found: {}", id);
                    return new ProductNotFoundException("Product not found");
                });

        return mapToResponse(product);
    }

    /**
     * Maps Product entity to ProductResponse DTO.
     */
    private ProductResponse mapToResponse(Product product) {

        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getCategory(),
                product.getPrice(),
                product.getStockQuantity(),
                product.getUnit(),
                product.isActive(),
                product.isRefundable()
        );
    }
}
