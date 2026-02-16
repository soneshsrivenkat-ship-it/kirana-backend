package com.example.demo.kafka.producer;

import com.example.demo.entity.Transaction;
import com.example.demo.events.TransactionEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * Kafka producer service responsible for publishing
 * transaction events to the messaging system.
 *
 * This service converts entities
 * into  objects and sends them
 * to the configured Kafka topic.
 *
 * Used for:
 *  - Reporting service updates
 *  - Analytics processing
 *  - Audit logging
 *  - Event-driven architecture integration
 */
@Service
@RequiredArgsConstructor
public class KafkaProducerService {

    /**
     * Spring Kafka template used to publish messages.
     */
    private final KafkaTemplate<String, TransactionEvent> kafkaTemplate;

    /**
     * Kafka topic name for transaction events.
     */
    private static final String TOPIC = "transactions.events";

    /**
     * Publishes a transaction event to Kafka.
     *
     * Converts the given {@link Transaction} entity
     * into a {@link TransactionEvent} and sends it
     * asynchronously to the configured topic.
     *
     * @param transaction The transaction entity to publish
     */
    public void sendTransactionEvent(Transaction transaction) {

        // Create event payload
        TransactionEvent event = new TransactionEvent();
        event.setTransactionId(transaction.getId());
        event.setUserId(transaction.getUserId());
        event.setTotalAmount(transaction.getTotalAmount());
        event.setCurrencyType(transaction.getCurrencyType());
        event.setTransactionType(transaction.getTransactionType());
        event.setDate(transaction.getDate());

        // Send event asynchronously to Kafka
        kafkaTemplate.send(TOPIC, event);
    }
}
