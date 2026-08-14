package com.pulsedesk.trading.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.pulsedesk.contracts.events.ExecutedOrderSide;
import com.pulsedesk.contracts.events.OrderCreated;
import com.pulsedesk.contracts.events.OrderExecuted;
import com.pulsedesk.contracts.events.OrderSide;
import com.pulsedesk.trading.client.MarketDataClient;
import com.pulsedesk.trading.dto.CreateOrderRequest;
import com.pulsedesk.trading.dto.MarketDataResponse;
import com.pulsedesk.trading.producer.OrderEventProducer;

@Service
public class TradingService {

    private final OrderEventProducer orderEventProducer;

    private final MarketDataClient marketDataClient;

    public TradingService(OrderEventProducer orderEventProducer, MarketDataClient marketDataClient) {
        this.orderEventProducer = orderEventProducer;
        this.marketDataClient = marketDataClient;
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

        ExecutedOrderSide executedSide = ExecutedOrderSide.valueOf(side.name());

        BigDecimal price = marketDataClient.getMarketData(symbol).price();

        String orderId = UUID.randomUUID().toString();

        OrderCreated eventCreation = OrderCreated.newBuilder()
                .setEventId(UUID.randomUUID().toString())
                .setOrderId(orderId)
                .setUserId(request.userId().toString())
                .setSymbol(symbol)
                .setSide(side)
                .setQuantity(request.quantity())
                .setTimestamp(Instant.now().toString())
                .build();

        OrderExecuted eventExecution = OrderExecuted.newBuilder()
                .setEventId(UUID.randomUUID().toString())
                .setOrderId(orderId)
                .setUserId(request.userId().toString())
                .setSymbol(symbol)
                .setSide(executedSide)
                .setQuantity(request.quantity())
                .setPrice(price)
                .setTimestamp(Instant.now().toString())
                .build();

        orderEventProducer.publishCreated(eventCreation);

        orderEventProducer.publishExecuted(eventExecution);
    }
}
