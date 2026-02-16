package com.example.demo.dao;

import com.example.demo.entity.TransactionItem;
import com.example.demo.repository.TransactionItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * TransactionItemDAO
 *
 * Data Access Layer for TransactionItem entity.
 *
 * This class acts as an abstraction layer between the
 * Service layer and the JPA repository.
 *
 * Responsibilities:
 * - Fetch transaction items by transaction ID
 * - Persist transaction items (sales & refund items)
 *
 * Used in:
 * - TransactionService (while creating sales)
 * - RefundService (while creating refund transactions)
 */
@Component
@RequiredArgsConstructor
public class TransactionItemDAO {

    /**
     * JPA Repository for TransactionItem entity.
     */
    private final TransactionItemRepository repository;

    /**
     * Fetch all items belonging to a specific transaction.
     *
     * Used for:
     * - Fetching original purchase items
     * - Checking already refunded items
     * - Building transaction response DTO
     *
     * @param transactionId Transaction ID (UUID)
     * @return List of TransactionItem
     */
    public List<TransactionItem> findByTransactionId(String transactionId) {
        return repository.findByTransactionId(transactionId);
    }

    /**
     * Save a transaction item.
     *
     * Used for:
     * - Saving purchase items during sales
     * - Saving refund items during refund flow
     *
     * @param item TransactionItem entity
     * @return Persisted TransactionItem
     */
    public TransactionItem save(TransactionItem item) {
        return repository.save(item);
    }
}
