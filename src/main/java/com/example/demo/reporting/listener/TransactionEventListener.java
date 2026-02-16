package com.example.demo.reporting.listener;

import com.example.demo.reporting.service.FinancialReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TransactionEventListener {

    private final FinancialReportService financialReportService;

    @KafkaListener(
            topics = "transactions.events",
            groupId = "reporting-group"
    )
    public void consume(String transactionId) {

        System.out.println("Received from Kafka: " + transactionId);

        financialReportService.processTransaction(transactionId);
    }
}
