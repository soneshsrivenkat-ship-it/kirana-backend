package com.example.demo.dao;

import com.example.demo.entity.Product;
import com.example.demo.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * ProductDAO
 *
 * Acts as an intermediate layer between the service layer and
 * ProductRepository. This class adds Redis caching behavior
 * on top of standard JPA repository operations.
 *
 * Cache Name: "products"
 *
 * Caching Strategy:
 * - Individual product is cached using product ID as key.
 * - Full product list is cached using key "ALL".
 * - On save/update → individual cache updated, list cache cleared.
 * - On delete → both individual and list cache cleared.
 */
@Component
@RequiredArgsConstructor
@CacheConfig(cacheNames = "products")
public class ProductDAO {

    /**
     * JPA Repository for Product entity.
     */
    private final ProductRepository repository;

    /**
     * Fetch product by ID.
     *
     * Cache Key: product ID
     *
     * Example:
     * findById("P001") → stored in cache as key "P001"
     *
     * @param id Product ID
     * @return Optional<Product>
     */
    @Cacheable(key = "#id")
    public Optional<Product> findById(String id) {
        return repository.findById(id);
    }

    /**
     * Fetch all products.
     *
     * Cache Key: "ALL"
     *
     * This avoids hitting database repeatedly for product listings.
     *
     * @return List of all products
     */
    @Cacheable(key = "'ALL'")
    public List<Product> findAll() {
        return repository.findAll();
    }

    /**
     * Save or update a product.
     *
     * Cache Behavior:
     * - Updates single product cache using product ID.
     * - Evicts full product list cache ("ALL") to maintain consistency.
     *
     * @param product Product entity to save
     * @return Saved Product
     */
    @Caching(
            put = {
                    @CachePut(key = "#result.id")
            },
            evict = {
                    @CacheEvict(key = "'ALL'")
            }
    )
    public Product save(Product product) {
        return repository.save(product);
    }

    /**
     * Delete product by ID.
     *
     * Cache Behavior:
     * - Removes individual product cache.
     * - Clears full list cache.
     *
     * @param id Product ID
     */
    @Caching(evict = {
            @CacheEvict(key = "#id"),
            @CacheEvict(key = "'ALL'")
    })
    public void deleteById(String id) {
        repository.deleteById(id);
    }
}
