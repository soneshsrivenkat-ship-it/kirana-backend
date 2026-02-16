package com.example.demo.repository;

import com.example.demo.entity.TransactionItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionItemRepository
        extends JpaRepository<TransactionItem, String> {


    List<TransactionItem> findByTransactionId(String transactionId);

}
