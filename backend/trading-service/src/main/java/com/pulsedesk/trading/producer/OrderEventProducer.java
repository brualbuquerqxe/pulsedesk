package com.pulsedesk.trading.producer;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.pulsedesk.contracts.events.OrderCreated;

@Component
public class OrderEventProducer {

    private final KafkaTemplate<String, OrderCreated> kafkaTemplate;

    public OrderEventProducer(
        KafkaTemplate<String, OrderCreated> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishCreated(OrderCreated event) {

        kafkaTemplate.send(
                "order.created",
                event.getOrderId().toString(),
                event);
    }
}
