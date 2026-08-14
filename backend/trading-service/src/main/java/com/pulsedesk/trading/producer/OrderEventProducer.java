package com.pulsedesk.trading.producer;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.pulsedesk.contracts.events.OrderCreated;
import com.pulsedesk.contracts.events.OrderExecuted;

@Component
public class OrderEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public OrderEventProducer(
            KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishCreated(OrderCreated event) {

        kafkaTemplate.send(
                "order.created",
                event.getOrderId().toString(),
                event);
    }

    public void publishExecuted(OrderExecuted event) {

        kafkaTemplate.send(
                "order.executed",
                event.getOrderId().toString(),
                event);
    }
}
