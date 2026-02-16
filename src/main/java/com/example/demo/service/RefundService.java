package com.example.demo.service;

import com.example.demo.dao.ProductDAO;
import com.example.demo.dao.TransactionDAO;
import com.example.demo.dao.TransactionItemDAO;
import com.example.demo.dto.TransactionItemResponse;
import com.example.demo.dto.TransactionResponse;
import com.example.demo.dto.RefundRequest;
import com.example.demo.entity.Product;
import com.example.demo.entity.Transaction;
import com.example.demo.entity.TransactionItem;
import com.example.demo.entity.TransactionType;
import com.example.demo.kafka.producer.KafkaProducerService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefundService {

    private final TransactionDAO transactionDAO;
    private final TransactionItemDAO itemDAO;
    private final ProductDAO productDAO;
    private final KafkaProducerService kafkaProducerService;

    /**
     * Processes a refund for a given transaction.
     * <p>
     * This method performs the following operations:
     * 1. Fetches the original transaction using the provided transaction ID.
     * 2. Retrieves all associated transaction items.
     * 3. Validates refundable items.
     * 4. Restores product stock quantities.
     * 5. Creates a new DEBIT transaction representing the refund.
     * 6. Saves corresponding refund transaction items.
     * 7. Updates the total refund amount.
     * <p>
     * The refund is executed as a transactional operation,
     * meaning all operations will roll back if any step fails.
     *
     * @param request contains the transaction ID to be refunded
     * @throws RuntimeException if transaction is not found
     * @throws RuntimeException if no items exist for the transaction
     */
    @Transactional
    @CacheEvict(value = {"transactions", "products"}, allEntries = true)
    public TransactionResponse refund(RefundRequest request) {

        String userId = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();


        Transaction originalTxn = transactionDAO
                .findById(request.getTransactionId())
                .orElseThrow(() ->
                        new RuntimeException("Transaction not found"));


        List<TransactionItem> originalItems =
                itemDAO.findByTransactionId(originalTxn.getId());

        if (originalItems.isEmpty()) {
            throw new RuntimeException("No items found for transaction");
        }


        Transaction refundTxn = new Transaction();
        refundTxn.setUserId(userId);
        refundTxn.setTransactionType(TransactionType.DEBIT);
        refundTxn.setCurrencyType(originalTxn.getCurrencyType());
        refundTxn.setTotalAmount(BigDecimal.ZERO);


        refundTxn = transactionDAO.save(refundTxn);

        BigDecimal totalRefund = BigDecimal.ZERO;
        List<TransactionItemResponse> refundedItems = new ArrayList<>();

        for (TransactionItem item : originalItems) {

            // Refund only selected products
            if (!request.getProductIds().contains(item.getProductId())) {
                continue;
            }

            // Check refundable
            if (!Boolean.TRUE.equals(item.getRefundable())) {
                throw new RuntimeException(
                        "Product not refundable: " + item.getProductName());
            }


            Product product = productDAO.findById(item.getProductId())
                    .orElseThrow(() ->
                            new RuntimeException("Product not found"));

            product.setStockQuantity(
                    product.getStockQuantity() + item.getPurchaseQuantity());

            productDAO.save(product);


            TransactionItem refundItem = new TransactionItem();
            refundItem.setId(UUID.randomUUID().toString());
            refundItem.setTransactionId(refundTxn.getId());
            refundItem.setProductId(item.getProductId());
            refundItem.setProductName(item.getProductName());
            refundItem.setPurchaseQuantity(item.getPurchaseQuantity());
            refundItem.setPriceAtPurchase(item.getPriceAtPurchase());
            refundItem.setRefundable(true);
            refundItem.setItemTotal(item.getItemTotal());

            itemDAO.save(refundItem);

            totalRefund = totalRefund.add(item.getItemTotal());

            refundedItems.add(
                    new TransactionItemResponse(
                            refundItem.getId(),
                            refundTxn.getId(),
                            refundItem.getProductId(),
                            refundItem.getProductName(),
                            refundItem.getPurchaseQuantity(),
                            refundItem.getPriceAtPurchase(),
                            refundItem.getRefundable(),
                            refundItem.getItemTotal()
                    )
            );
        }

        if (refundedItems.isEmpty()) {
            throw new RuntimeException("No valid products selected for refund");
        }


        refundTxn.setTotalAmount(totalRefund);
        transactionDAO.save(refundTxn);
        transactionDAO.flush();

        kafkaProducerService.sendTransactionEvent(refundTxn);

        return new TransactionResponse(
                refundTxn.getId(),
                refundTxn.getUserId(),
                refundTxn.getTotalAmount(),
                refundTxn.getTransactionType(),
                refundTxn.getCurrencyType(),
                refundTxn.getDate(),
                refundedItems
        );
    }
}