package com.example.demo.kafka.producer;

import com.example.demo.entity.Transaction;
import com.example.demo.events.TransactionEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaProducerService {

    private final KafkaTemplate<String, TransactionEvent> kafkaTemplate;

    private static final String TOPIC = "transactions.events";

    public void sendTransactionEvent(Transaction transaction) {

        TransactionEvent event = new TransactionEvent();
        event.setTransactionId(transaction.getId());
        event.setUserId(transaction.getUserId());
        event.setTotalAmount(transaction.getTotalAmount());
        event.setCurrencyType(transaction.getCurrencyType());
        event.setTransactionType(transaction.getTransactionType());
        event.setDate(transaction.getDate());

        kafkaTemplate.send(TOPIC, event);


    }
}
