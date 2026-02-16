package com.example.demo.reporting.consumer;

import com.example.demo.entity.TransactionType;
import com.example.demo.events.TransactionEvent;
import com.example.demo.reporting.document.FinancialReport;
import com.example.demo.reporting.repo.FinancialReportRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.IsoFields;

/**
 * Kafka consumer responsible for processing transaction events
 * and generating financial reports in MongoDB.
 *
 * This service listens to transaction events and updates:
 *  - Weekly reports
 *  - Monthly reports
 *  - Yearly reports
 *
 * Reports are stored in MongoDB and updated incrementally.
 */
@Service
public class ReportingConsumer {

    private final FinancialReportRepository repository;

    /**
     * Constructor-based dependency injection.
     */
    public ReportingConsumer(FinancialReportRepository repository) {
        this.repository = repository;
    }

    /**
     * Kafka listener that consumes transaction events.
     *
     * For every transaction event received:
     *  - Weekly report is updated
     *  - Monthly report is updated
     *  - Yearly report is updated
     *
     * @param event The transaction event received from Kafka
     */
    @KafkaListener(topics = "transactions.events", groupId = "reporting-group")
    public void consume(TransactionEvent event) {

        System.out.println("Received Kafka event for user: " + event.getUserId());

        // Generate weekly period (e.g., 2026-W07)
        String weeklyPeriod = event.getDate().getYear() + "-W" +
                String.format("%02d",
                        event.getDate().get(IsoFields.WEEK_OF_WEEK_BASED_YEAR));

        updateReport(event, "WEEKLY", weeklyPeriod);

        // Generate monthly period (e.g., 2026-02)
        String monthlyPeriod = event.getDate()
                .format(DateTimeFormatter.ofPattern("yyyy-MM"));

        updateReport(event, "MONTHLY", monthlyPeriod);

        // Generate yearly period (e.g., 2026)
        String yearlyPeriod = String.valueOf(event.getDate().getYear());

        updateReport(event, "YEARLY", yearlyPeriod);
    }

    /**
     * Updates or creates a financial report for a given period.
     *
     * @param event      Transaction event data
     * @param periodType WEEKLY / MONTHLY / YEARLY
     * @param period     Formatted period string
     */
    private void updateReport(TransactionEvent event,
                              String periodType,
                              String period) {

        FinancialReport report = repository
                .findByUserIdAndPeriodTypeAndPeriodAndCurrencyType(
                        event.getUserId(),
                        periodType,
                        period,
                        event.getCurrencyType()
                )
                .orElseGet(() -> {
                    FinancialReport newReport = new FinancialReport();
                    newReport.setUserId(event.getUserId());
                    newReport.setCurrencyType(event.getCurrencyType());
                    newReport.setPeriodType(periodType);
                    newReport.setPeriod(period);
                    newReport.setTotalCredits(BigDecimal.ZERO);
                    newReport.setTotalDebits(BigDecimal.ZERO);
                    newReport.setNetFlow(BigDecimal.ZERO);
                    return newReport;
                });

        // Ensure values are initialized
        if (report.getTotalCredits() == null) {
            report.setTotalCredits(BigDecimal.ZERO);
        }
        if (report.getTotalDebits() == null) {
            report.setTotalDebits(BigDecimal.ZERO);
        }

        // Update totals based on transaction type
        if (event.getTransactionType() == TransactionType.CREDIT) {
            report.setTotalCredits(
                    report.getTotalCredits().add(event.getTotalAmount())
            );
        } else {
            report.setTotalDebits(
                    report.getTotalDebits().add(event.getTotalAmount())
            );
        }

        // Compute net flow
        report.setNetFlow(
                report.getTotalCredits().subtract(report.getTotalDebits())
        );

        report.setLastUpdated(LocalDateTime.now());

        repository.save(report);

        System.out.println(periodType + " report saved successfully");
    }
}
