package com.example.demo.dao;

import com.example.demo.entity.TransactionItem;
import com.example.demo.repository.TransactionItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor

public class TransactionItemDAO {

    private final TransactionItemRepository repository;


    public List<TransactionItem> findByTransactionId(String transactionId) {
        return repository.findByTransactionId(transactionId);
    }


    public TransactionItem save(TransactionItem item) {
        return repository.save(item);
    }
}
