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

@Service
public class ReportingConsumer {

    private final FinancialReportRepository repository;

    public ReportingConsumer(FinancialReportRepository repository) {
        this.repository = repository;
    }

    @KafkaListener(topics = "transactions.events", groupId = "reporting-group")
    public void consume(TransactionEvent event) {

        System.out.println("Received Kafka event for user: " + event.getUserId());


        String weeklyPeriod = event.getDate().getYear() + "-W" +
                String.format("%02d",
                        event.getDate().get(IsoFields.WEEK_OF_WEEK_BASED_YEAR));

        updateReport(event, "WEEKLY", weeklyPeriod);


        String monthlyPeriod = event.getDate()
                .format(DateTimeFormatter.ofPattern("yyyy-MM"));

        updateReport(event, "MONTHLY", monthlyPeriod);


        String yearlyPeriod = String.valueOf(event.getDate().getYear());

        updateReport(event, "YEARLY", yearlyPeriod);
    }


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


        if (report.getTotalCredits() == null) {
            report.setTotalCredits(BigDecimal.ZERO);
        }
        if (report.getTotalDebits() == null) {
            report.setTotalDebits(BigDecimal.ZERO);
        }

        // Update totals
        if (event.getTransactionType() == TransactionType.CREDIT) {
            report.setTotalCredits(
                    report.getTotalCredits().add(event.getTotalAmount())
            );
        } else {
            report.setTotalDebits(
                    report.getTotalDebits().add(event.getTotalAmount())
            );
        }


        report.setNetFlow(
                report.getTotalCredits().subtract(report.getTotalDebits())
        );

        report.setLastUpdated(LocalDateTime.now());

        repository.save(report);

        System.out.println(periodType + " report saved successfully");
    }
}
