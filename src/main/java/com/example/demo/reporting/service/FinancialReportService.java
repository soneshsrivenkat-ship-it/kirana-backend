package com.example.demo.reporting.service;

import com.example.demo.dao.TransactionDAO;
import com.example.demo.entity.Transaction;
import com.example.demo.entity.TransactionType;
import com.example.demo.reporting.document.FinancialReport;
import com.example.demo.reporting.repo.FinancialReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

/**
 * Service responsible for managing financial reports.
 *
 * Handles:
 *  - Manual report processing
 *  - Report lookup operations
 *  - Monthly aggregation logic
 */
@Service
@RequiredArgsConstructor
public class FinancialReportService {

    private final TransactionDAO transactionDAO;
    private final FinancialReportRepository reportRepository;

    /**
     * Processes a transaction and updates
     * the corresponding monthly financial report.
     *
     * @param transactionId ID of the transaction to process
     */
    public void processTransaction(String transactionId) {

        Transaction txn = transactionDAO.findById(transactionId)
                .orElseThrow();

        String period = txn.getDate()
                .format(DateTimeFormatter.ofPattern("yyyy-MM"));

        FinancialReport report =
                reportRepository.findByUserIdAndPeriod(
                        txn.getUserId(),
                        period
                ).orElseGet(() -> {
                    FinancialReport newReport = new FinancialReport();
                    newReport.setUserId(txn.getUserId());
                    newReport.setCurrencyType(txn.getCurrencyType());
                    newReport.setPeriodType("MONTHLY");
                    newReport.setPeriod(period);
                    newReport.setTotalCredits(BigDecimal.ZERO);
                    newReport.setTotalDebits(BigDecimal.ZERO);
                    newReport.setNetFlow(BigDecimal.ZERO);
                    return newReport;
                });

        if (txn.getTransactionType() == TransactionType.CREDIT) {
            report.setTotalCredits(
                    report.getTotalCredits().add(txn.getTotalAmount())
            );
        } else {
            report.setTotalDebits(
                    report.getTotalDebits().add(txn.getTotalAmount())
            );
        }

        report.setNetFlow(
                report.getTotalCredits()
                        .subtract(report.getTotalDebits())
        );

        reportRepository.save(report);

        System.out.println("Updated Mongo Financial Report");
    }

    /**
     * Fetch report by user, period type, period, and currency.
     */
    public Optional<FinancialReport>
    findByUserIdAndPeriodTypeAndPeriodAndCurrencyType(
            String userId,
            String periodType,
            String period,
            String currency) {

        return reportRepository
                .findByUserIdAndPeriodTypeAndPeriodAndCurrencyType(
                        userId, periodType, period, currency);
    }
}
