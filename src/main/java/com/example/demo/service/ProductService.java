package com.example.demo.service;
import com.example.demo.dto.ProductRequest;
import com.example.demo.dto.ProductResponse;
import com.example.demo.entity.Product;
import com.example.demo.exception.ProductNotFoundException;
import com.example.demo.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepo;

    /**
     * Creates a new product in the system.
     *
     * This method maps the incoming ProductRequest DTO
     * to a Product entity, generates a unique ID,
     * saves it to the database, and returns a
     * ProductResponse DTO.
     *
     * @param request the product details received from the client
     * @return the created product wrapped in ProductResponse
     */
    @CacheEvict(value = "products", allEntries = true)
    public ProductResponse createProduct(ProductRequest request) {

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

        return mapToResponse(product);
    }

    /**
     * Updates an existing product.
     *
     * This method fetches the product by ID,
     * updates its fields with the provided request data,
     * and saves the updated entity to the database.
     *
     * @param id the ID of the product to update
     * @param request the updated product details
     * @return the updated product as ProductResponse
     * @throws ProductNotFoundException if product does not exist
     */
    @CacheEvict(value = "products", allEntries = true)
    public ProductResponse updateProduct(
            String id,
            ProductRequest request) {

        Product product = productRepo.findById(id)
                .orElseThrow(() ->
                        new ProductNotFoundException("Product not found"));

        product.setName(request.getName());
        product.setCategory(request.getCategory());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());
        product.setUnit(request.getUnit());
        product.setActive(request.getActive());
        product.setRefundable(request.getRefundable());

        productRepo.save(product);

        return mapToResponse(product);
    }

    /**
     * Retrieves a product by its ID.
     *
     * This method fetches the product entity from the database
     * and converts it into a ProductResponse DTO.
     * @return the product details as ProductResponse
     * @throws ProductNotFoundException if product is not found
     */
    @Cacheable(value = "products", key = "'all'")
    public List<ProductResponse> getAllProducts() {

        return productRepo.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a product by its ID.
     *
     * This method fetches the product entity from the database
     * and converts it into a ProductResponse DTO.
     *
     * @param id the product ID
     * @return the product details as ProductResponse
     * @throws ProductNotFoundException if product is not found
     */
    @Cacheable(value = "products", key = "#id")
    public ProductResponse getProductById(String id) {

        Product product = productRepo.findById(id)
                .orElseThrow(() ->
                        new ProductNotFoundException("Product not found"));

        return mapToResponse(product);
    }

    /**
     * Converts a Product entity into a ProductResponse DTO.
     *
     * This method is used internally to separate
     * entity structure from API response structure.
     *
     * @param product the Product entity
     * @return mapped ProductResponse DTO
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
