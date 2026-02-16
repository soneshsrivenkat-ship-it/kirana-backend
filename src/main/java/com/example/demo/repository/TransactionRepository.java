package com.example.demo.repository;

import com.example.demo.entity.Transaction;
import com.example.demo.entity.TransactionType;
import io.micrometer.common.KeyValues;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepository
        extends JpaRepository<Transaction, String> {


    /**
     * Return the transaction based on the currency type.
     * @param currencyType parameter used to find the transactions.
     * @return Returns a list of transactions based on the currency type used.
     */
    List<Transaction> findByCurrencyType(String currencyType);

    /**
     * Used to return the transactions based on the from and to time.
     * @param from Parameter used to fetch the transactions
     * @param to Parameter used to fetch the transactions
     * @return All transactions based on the timeFrame.
     */
    @Query("""
       SELECT t
       FROM Transaction t
       WHERE t.date BETWEEN :from AND :to
       """)
    List<Transaction> findByDate(
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );

    List<Transaction> findByParentTransactionId(String parentTransactionId);
}
