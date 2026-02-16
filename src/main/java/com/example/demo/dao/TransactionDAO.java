package com.example.demo.dao;

import com.example.demo.entity.Transaction;
import com.example.demo.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * TransactionDAO
 *
 * Acts as a data access layer between the Service layer and
 * TransactionRepository (JPA).
 *
 * This class centralizes database operations related to
 * Transaction entity.
 *
 * Responsibilities:
 * - Fetch transaction by ID
 * - Fetch refund transactions using parentTransactionId
 * - Save transactions (CREDIT / DEBIT)
 * - Filter transactions by currency
 * - Filter transactions by time range
 * - Flush persistence context when required
 */
@Component
@RequiredArgsConstructor
public class TransactionDAO {

    /**
     * JPA Repository for Transaction entity.
     */
    private final TransactionRepository repository;

    /**
     * Fetch transaction by ID.
     *
     * @param id Transaction ID (UUID)
     * @return Optional<Transaction>
     */
    public Optional<Transaction> findById(String id) {
        return repository.findById(id);
    }

    /**
     * Fetch all refund transactions linked to a specific
     * original transaction.
     *
     * Used to prevent double refunds.
     *
     * @param parentTransactionId Original transaction ID
     * @return List of refund transactions
     */
    public List<Transaction> findByParentTransactionId(String parentTransactionId) {
        return repository.findByParentTransactionId(parentTransactionId);
    }

    /**
     * Fetch all transactions from database.
     *
     * @return List of transactions
     */
    public List<Transaction> findAll() {
        return repository.findAll();
    }

    /**
     * Save a transaction.
     *
     * Used for:
     * - Creating CREDIT transaction (sales)
     * - Creating DEBIT transaction (refund)
     * - Updating totalAmount
     *
     * @param txn Transaction entity
     * @return Persisted Transaction
     */
    public Transaction save(Transaction txn) {
        return repository.save(txn);
    }

    /**
     * Fetch transactions filtered by currency type.
     *
     * @param currency Currency code (e.g., INR, USD)
     * @return List of transactions using that currency
     */
    public List<Transaction> findByCurrency(String currency) {
        return repository.findByCurrencyType(currency);
    }

    /**
     * Fetch transactions between two timestamps.
     *
     * Used for reporting and analytics.
     *
     * @param from Start date-time
     * @param to   End date-time
     * @return List of transactions within timeframe
     */
    public List<Transaction> findByTime(
            LocalDateTime from,
            LocalDateTime to) {
        return repository.findByDate(from, to);
    }

    /**
     * Flush persistence context immediately.
     *
     * Forces Hibernate to synchronize the current state
     * with the database.
     *
     * Used when:
     * - Immediate DB consistency is required
     * - Before executing dependent queries
     */
    public void flush() {
        repository.flush();
    }
}
