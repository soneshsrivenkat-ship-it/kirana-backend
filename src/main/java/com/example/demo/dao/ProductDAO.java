package com.example.demo.dao;

import com.example.demo.entity.Product;
import com.example.demo.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@CacheConfig(cacheNames = "productCache") // default cache name
public class ProductDAO {

    private final ProductRepository repository;

    // ==============================
    // Cache Validation (Read)
    // ==============================
    @Cacheable(key = "#id")
    public Optional<Product> findById(String id) {
        return repository.findById(id);
    }

    @Cacheable(cacheNames = "productsAllCache", key = "'ALL'")
    public List<Product> findAll() {
        return repository.findAll();
    }

    // ==============================
    // Cache Invalidation (Write)
    // ==============================
    @Caching(
            put = {
                    @CachePut(key = "#result.id") // update single product cache
            },
            evict = {
                    @CacheEvict(cacheNames = "productsAllCache", key = "'ALL'") // clear list cache
            }
    )
    public Product save(Product product) {
        return repository.save(product);
    }

    @Caching(evict = {
            @CacheEvict(key = "#id"),
            @CacheEvict(cacheNames = "productsAllCache", key = "'ALL'")
    })
    public void deleteById(String id) {
        repository.deleteById(id);
    }
}