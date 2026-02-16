package com.example.demo.service;

import com.example.demo.dto.SalesSummaryResponse;
import com.example.demo.entity.Transaction;
import com.example.demo.entity.TransactionType;
import com.example.demo.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SalesService {


    private final TransactionRepository txnRepo;
    private final CurrencyServiceImpl currencyServiceImpl;
    /**
     * Retrieves the sales summary based on the provided currency.
     *
     * This endpoint calculates and returns:
     * - Total sales amount
     * - Total number of transactions
     * - Total debit sales and count
     * - Total credit sales and count
     *
     * If no currency is provided, the default currency (INR) is used.
     *
     * @param targetCurrency the currency in which the sales summary
     *                 should be calculated (default: INR)
     * @return ApiResponse containing summarized sales data
     */

    public SalesSummaryResponse getSalesSummary(String targetCurrency) {

        List<Transaction> transactions = txnRepo.findAll();

        BigDecimal creditTotal = BigDecimal.ZERO;
        BigDecimal debitTotal = BigDecimal.ZERO;

        Long creditCount = 0L;
        Long debitCount = 0L;

        for (Transaction txn : transactions) {

            BigDecimal converted =
                    currencyServiceImpl.convert(
                            txn.getTotalAmount(),
                            txn.getCurrencyType(),
                            targetCurrency
                    );

            if (txn.getTransactionType() == TransactionType.CREDIT) {
                creditTotal = creditTotal.add(converted);
                creditCount++;
            }

            if (txn.getTransactionType() == TransactionType.DEBIT) {
                debitTotal = debitTotal.add(converted);
                debitCount++;
            }
        }

        BigDecimal netSales = creditTotal.subtract(debitTotal);

        return new SalesSummaryResponse(
                targetCurrency,
                netSales,
                creditTotal,
                debitTotal,
                creditCount + debitCount,
                creditCount,
                debitCount
        );
    }
}
