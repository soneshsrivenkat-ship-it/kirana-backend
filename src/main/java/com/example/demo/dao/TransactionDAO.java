package com.example.demo.dao;

import com.example.demo.entity.Transaction;
import com.example.demo.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.*;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor

public class TransactionDAO {

    private final TransactionRepository repository;


    public Optional<Transaction> findById(String id) {
        return repository.findById(id);
    }


    public List<Transaction> findAll() {
        return repository.findAll();
    }


    public Transaction save(Transaction txn) {
        return repository.save(txn);
    }

    public List<Transaction> findByCurrency(String currency) {
        return repository.findByCurrencyType(currency);
    }


    public List<Transaction> findByTime(
            LocalDateTime from,
            LocalDateTime to) {
        return repository.findByDate(from, to);
    }


    public void flush() {
        repository.flush();
    }
}
