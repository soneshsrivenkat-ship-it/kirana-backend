package com.example.demo.service;
import com.esotericsoftware.kryo.util.Null;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import com.example.demo.dao.ProductDAO;
import com.example.demo.dao.TransactionDAO;
import com.example.demo.dao.TransactionItemDAO;
import com.example.demo.dto.*;
import com.example.demo.dto.*;
import com.example.demo.entity.Product;
import com.example.demo.entity.Transaction;
import com.example.demo.entity.TransactionItem;
import com.example.demo.entity.TransactionType;
import com.example.demo.kafka.producer.KafkaProducerService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = "transactions")
public class TransactionService {

    private final ProductDAO productDAO;
    private final TransactionDAO transactionDAO;
    private final TransactionItemDAO transactionItemDAO;
    private final KafkaProducerService kafkaProducerService;



    @Transactional
    @CacheEvict(allEntries = true)
    public TransactionResponse createTransaction(TransactionRequest request) {

        String userId = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        Transaction txn = new Transaction();
        txn.setUserId(userId);
        txn.setTransactionType(TransactionType.CREDIT);
        txn.setCurrencyType(request.getCurrencyType());
        txn.setTotalAmount(BigDecimal.ZERO);
        txn.setParentTransactionId(null);

        txn = transactionDAO.save(txn);

        String transactionId = txn.getId();
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<TransactionItemResponse> itemResponses = new ArrayList<>();

        for (TransactionItemRequest itemReq : request.getItems()) {

            Product product = productDAO.findById(itemReq.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            if (product.getStockQuantity() < itemReq.getQuantity()) {
                throw new RuntimeException("Insufficient stock");
            }

            product.setStockQuantity(
                    product.getStockQuantity() - itemReq.getQuantity());

            productDAO.save(product);

            BigDecimal itemTotal =
                    product.getPrice()
                            .multiply(BigDecimal.valueOf(itemReq.getQuantity()));

            totalAmount = totalAmount.add(itemTotal);

            TransactionItem item = new TransactionItem();
            item.setId(UUID.randomUUID().toString());
            item.setTransactionId(transactionId);
            item.setProductId(product.getId());
            item.setProductName(product.getName());
            item.setPurchaseQuantity(itemReq.getQuantity());
            item.setPriceAtPurchase(product.getPrice());
            item.setRefundable(product.isRefundable());
            item.setItemTotal(itemTotal);

            transactionItemDAO.save(item);
        }

        txn.setTotalAmount(totalAmount);
        transactionDAO.save(txn);
        transactionDAO.flush();

        kafkaProducerService.sendTransactionEvent(txn);

        return mapToResponse(txn);
    }


    @Cacheable(key = "#id")
    public TransactionResponse getTransactionById(String id) {

        Transaction txn = transactionDAO.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        return mapToResponse(txn);
    }


    @Cacheable(key = "'ALL'")
    public List<TransactionResponse> getAllTransactions() {

        return transactionDAO.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


    @Cacheable(key = "'CURRENCY_' + #currency")
    public List<TransactionResponse> getByCurrency(String currency) {

        return transactionDAO.findByCurrency(currency)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


    @Cacheable(
            key = "'TIME_' + #from.toString() + '_' + #to.toString()")
    public List<TransactionResponse> getByTime(
            LocalDateTime from,
            LocalDateTime to) {

        return transactionDAO.findByTime(from, to)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


    private TransactionResponse mapToResponse(Transaction txn) {

        List<TransactionItem> items =
                transactionItemDAO.findByTransactionId(txn.getId());

        List<TransactionItemResponse> itemResponses = new ArrayList<>();

        for (TransactionItem item : items) {

            itemResponses.add(
                    new TransactionItemResponse(
                            item.getId(),
                            item.getTransactionId(),
                            item.getProductId(),
                            item.getProductName(),
                            item.getPurchaseQuantity(),
                            item.getPriceAtPurchase(),
                            item.getRefundable(),
                            item.getItemTotal()
                    )
            );
        }

        return new TransactionResponse(
                txn.getId(),
                txn.getUserId(),
                txn.getTotalAmount(),
                txn.getTransactionType(),
                txn.getCurrencyType(),
                txn.getParentTransactionId(),
                txn.getDate(),
                itemResponses
        );
    }
}