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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RefundService {

    private final TransactionDAO transactionDAO;
    private final TransactionItemDAO itemDAO;
    private final ProductDAO productDAO;
    private final KafkaProducerService kafkaProducerService;

    @Transactional
    @CacheEvict(value = {"transactions"}, allEntries = true)
    public TransactionResponse refund(RefundRequest request) {

        // ==========================
        // 1️⃣ Validate Authentication
        // ==========================
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            throw new RuntimeException("User not authenticated");
        }

        String userId = auth.getName();

        // ==========================
        // 2️⃣ Validate Request
        // ==========================
        if (request.getProductIds() == null || request.getProductIds().isEmpty()) {
            throw new RuntimeException("Product list cannot be empty");
        }

        // ==========================
        // 3️⃣ Fetch Original Transaction
        // ==========================
        Transaction originalTxn = transactionDAO
                .findById(request.getTransactionId())
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        if (originalTxn.getTransactionType() != TransactionType.CREDIT) {
            throw new RuntimeException("Refund allowed only for CREDIT transactions");
        }

        // ==========================
        // 4️⃣ Prevent Double Refund
        // ==========================
        List<Transaction> existingRefunds =
                transactionDAO.findByParentTransactionId(originalTxn.getId());

        Set<String> alreadyRefundedProducts = new HashSet<>();

        for (Transaction refundTxn : existingRefunds) {
            List<TransactionItem> refundedItems =
                    itemDAO.findByTransactionId(refundTxn.getId());

            refundedItems.forEach(i ->
                    alreadyRefundedProducts.add(i.getProductId()));
        }

        // ==========================
        // 5️⃣ Get Original Items
        // ==========================
        List<TransactionItem> originalItems =
                itemDAO.findByTransactionId(originalTxn.getId());

        if (originalItems.isEmpty()) {
            throw new RuntimeException("No items found for transaction");
        }

        Map<String, TransactionItem> itemMap =
                originalItems.stream()
                        .collect(Collectors.toMap(
                                TransactionItem::getProductId,
                                i -> i
                        ));

        // ==========================
        // 6️⃣ Create Refund Transaction
        // ==========================
        Transaction refundTxn = new Transaction();
        refundTxn.setUserId(userId);
        refundTxn.setTransactionType(TransactionType.DEBIT);
        refundTxn.setCurrencyType(originalTxn.getCurrencyType());
        refundTxn.setParentTransactionId(originalTxn.getId());
        refundTxn.setTotalAmount(BigDecimal.ZERO);

        refundTxn = transactionDAO.save(refundTxn);

        BigDecimal totalRefund = BigDecimal.ZERO;
        List<TransactionItemResponse> refundedItems = new ArrayList<>();

        // ==========================
        // 7️⃣ Process Each Product
        // ==========================
        for (String productId : request.getProductIds()) {

            if (!itemMap.containsKey(productId)) {
                throw new RuntimeException(
                        "Product does not belong to original transaction: " + productId);
            }

            if (alreadyRefundedProducts.contains(productId)) {
                throw new RuntimeException(
                        "Product already refunded: " + productId);
            }

            TransactionItem originalItem = itemMap.get(productId);

            if (!Boolean.TRUE.equals(originalItem.getRefundable())) {
                throw new RuntimeException(
                        "Product not refundable: " + originalItem.getProductName());
            }

            Product product = productDAO.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            // Restore stock
            product.setStockQuantity(
                    product.getStockQuantity() + originalItem.getPurchaseQuantity());

            productDAO.save(product);

            // Create refund item
            TransactionItem refundItem = new TransactionItem();
            refundItem.setId(UUID.randomUUID().toString());
            refundItem.setTransactionId(refundTxn.getId());
            refundItem.setProductId(originalItem.getProductId());
            refundItem.setProductName(originalItem.getProductName());
            refundItem.setPurchaseQuantity(originalItem.getPurchaseQuantity());
            refundItem.setPriceAtPurchase(originalItem.getPriceAtPurchase());
            refundItem.setRefundable(true);
            refundItem.setItemTotal(originalItem.getItemTotal());

            itemDAO.save(refundItem);

            totalRefund = totalRefund.add(originalItem.getItemTotal());

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

        // ==========================
        // 8️⃣ Finalize Refund
        // ==========================
        refundTxn.setTotalAmount(totalRefund);
        transactionDAO.save(refundTxn);

        kafkaProducerService.sendTransactionEvent(refundTxn);

        return new TransactionResponse(
                refundTxn.getId(),
                refundTxn.getUserId(),
                refundTxn.getTotalAmount(),
                refundTxn.getTransactionType(),
                refundTxn.getCurrencyType(),
                refundTxn.getParentTransactionId(),
                refundTxn.getDate(),
                refundedItems
        );
    }
}
