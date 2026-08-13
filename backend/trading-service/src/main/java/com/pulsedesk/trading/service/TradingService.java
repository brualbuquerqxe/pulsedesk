package com.pulsedesk.trading.service;

import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.pulsedesk.contracts.events.OrderCreated;
import com.pulsedesk.contracts.events.OrderSide;
import com.pulsedesk.trading.dto.CreateOrderRequest;
import com.pulsedesk.trading.producer.OrderEventProducer;

@Service
public class TradingService {

    private final OrderEventProducer orderEventProducer;

    public TradingService(OrderEventProducer orderEventProducer) {
        this.orderEventProducer = orderEventProducer;
    }

    public void createOrder(CreateOrderRequest request) {

        String symbol = request.symbol()
                .trim()
                .toUpperCase();

        OrderSide side;

        try {
            side = OrderSide.valueOf(
                    request.side()
                            .trim()
                            .toUpperCase());
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException(
                    "Order side must be BUY or SELL");
        }

        String orderId = UUID.randomUUID().toString();

        OrderCreated event = OrderCreated.newBuilder()
                .setEventId(UUID.randomUUID().toString())
                .setOrderId(orderId)
                .setUserId(request.userId().toString())
                .setSymbol(symbol)
                .setSide(side)
                .setQuantity(request.quantity())
                .setTimestamp(Instant.now().toString())
                .build();

        orderEventProducer.publishCreated(event);
    }
}
