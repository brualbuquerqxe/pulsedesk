package com.pulsedesk.gateway.consumer;

import java.util.UUID;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import com.pulsedesk.contracts.events.OrderCreated;
import com.pulsedesk.contracts.events.OrderExecuted;
import com.pulsedesk.contracts.events.OrderRejected;
import com.pulsedesk.gateway.dto.OrderWebSocketMessage;

@Component
public class OrderEventConsumer {

    private final SimpMessagingTemplate simpMessagingTemplate;

    public OrderEventConsumer(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @KafkaListener(topics = "order.created", groupId = "websocket-gateway")
    public void consumeCreated(OrderCreated event) {

        OrderWebSocketMessage message = new OrderWebSocketMessage(
                UUID.fromString(event.getOrderId().toString()),
                UUID.fromString(event.getUserId().toString()),
                event.getSymbol().toString(),
                event.getSide().toString(),
                "CREATED",
                event.getQuantity(),
                null,
                null,
                event.getTimestamp().toString());

        simpMessagingTemplate.convertAndSend(
                "/topic/orders",
                message);
    }

    @KafkaListener(topics = "order.executed", groupId = "websocket-gateway")
    public void consumeExecuted(OrderExecuted event) {

        OrderWebSocketMessage message = new OrderWebSocketMessage(
                UUID.fromString(event.getOrderId().toString()),
                UUID.fromString(event.getUserId().toString()),
                event.getSymbol().toString(),
                event.getSide().toString(),
                "EXECUTED",
                event.getQuantity(),
                event.getPrice(),
                null,
                event.getTimestamp().toString());

        simpMessagingTemplate.convertAndSend(
                "/topic/orders",
                message);
    }

    @KafkaListener(topics = "order.rejected", groupId = "websocket-gateway")
    public void consumeRejected(OrderRejected event) {

        OrderWebSocketMessage message = new OrderWebSocketMessage(
                UUID.fromString(event.getOrderId().toString()),
                UUID.fromString(event.getUserId().toString()),
                event.getSymbol().toString(),
                event.getSide().toString(),
                "REJECTED",
                event.getQuantity(),
                null,
                event.getReason().toString(),
                event.getTimestamp().toString());

        simpMessagingTemplate.convertAndSend(
                "/topic/orders",
                message);
    }
}
