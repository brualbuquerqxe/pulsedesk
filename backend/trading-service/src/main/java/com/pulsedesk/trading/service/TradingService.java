package com.pulsedesk.trading.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.pulsedesk.contracts.events.ExecutedOrderSide;
import com.pulsedesk.contracts.events.OrderCreated;
import com.pulsedesk.contracts.events.OrderExecuted;
import com.pulsedesk.contracts.events.OrderSide;
import com.pulsedesk.trading.client.MarketDataClient;
import com.pulsedesk.trading.dto.CreateOrderRequest;
import com.pulsedesk.trading.dto.OrderResponse;
import com.pulsedesk.trading.dto.PortfolioResponse;
import com.pulsedesk.trading.dto.PositionResponse;
import com.pulsedesk.trading.entity.Order;
import com.pulsedesk.trading.entity.OrderStatus;
import com.pulsedesk.trading.entity.TradeSide;
import com.pulsedesk.trading.producer.OrderEventProducer;
import com.pulsedesk.trading.repository.OrderRepository;
import com.pulsedesk.contracts.events.RejectedOrderSide;
import com.pulsedesk.trading.client.PortfolioClient;
import com.pulsedesk.contracts.events.OrderRejected;
import org.springframework.web.client.RestClientException;

@Service
public class TradingService {

        private final OrderEventProducer orderEventProducer;

        private final MarketDataClient marketDataClient;

        private final OrderRepository orderRepository;

        private final PortfolioClient portfolioClient;

        public TradingService(OrderEventProducer orderEventProducer, MarketDataClient marketDataClient,
                        OrderRepository orderRepository, PortfolioClient portfolioClient) {
                this.orderEventProducer = orderEventProducer;
                this.marketDataClient = marketDataClient;
                this.orderRepository = orderRepository;
                this.portfolioClient = portfolioClient;
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

                // Para o banco
                TradeSide tradeSide = TradeSide.valueOf(side.name());

                Instant now = Instant.now();

                ExecutedOrderSide executedSide = ExecutedOrderSide.valueOf(side.name());

                // Identifica a ordem no banco
                UUID id = UUID.randomUUID();

                UUID orderId = UUID.randomUUID();

                Order order = new Order(id, orderId, request.userId(), symbol, tradeSide, request.quantity(),
                                OrderStatus.CREATED, null, now, now);

                OrderCreated eventCreation = OrderCreated.newBuilder()
                                .setEventId(UUID.randomUUID().toString())
                                .setOrderId(orderId.toString())
                                .setUserId(request.userId().toString())
                                .setSymbol(symbol)
                                .setSide(side)
                                .setQuantity(request.quantity())
                                .setTimestamp(now.toString())
                                .build();

                // Salva ordem no banco
                orderRepository.save(order);

                orderEventProducer.publishCreated(eventCreation);

                BigDecimal price;

                try {
                        price = marketDataClient.getMarketData(symbol).price();
                } catch (RestClientException exception) {

                        RejectedOrderSide rejectedSide = RejectedOrderSide.valueOf(side.name());

                        String reason = "Unable to retrieve market data for symbol";

                        order.setStatus(OrderStatus.REJECTED);
                        order.setUpdatedAt(Instant.now());
                        order.setRejectionReason(reason);
                        orderRepository.save(order);

                        OrderRejected eventRejected = OrderRejected.newBuilder()
                                        .setEventId(UUID.randomUUID().toString())
                                        .setOrderId(orderId.toString())
                                        .setUserId(request.userId().toString())
                                        .setSymbol(symbol)
                                        .setSide(rejectedSide)
                                        .setReason(reason)
                                        .setQuantity(request.quantity())
                                        .setTimestamp(Instant.now().toString())
                                        .build();

                        orderEventProducer.publishRejected(eventRejected);

                        return;
                }

                BigDecimal totalPriceOrder = price.multiply(BigDecimal.valueOf(order.getQuantity()));
                PortfolioResponse portfolio = portfolioClient.getPortfolioResponse(request.userId());

                BigDecimal cashBalance = portfolio.cashBalance();

                PositionResponse position = portfolio.positions()
                                .stream()
                                .filter(pos -> pos.symbol().equals(symbol))
                                .findFirst()
                                .orElse(null);

                if (tradeSide == TradeSide.SELL &&
                                (position == null || position.quantity() < request.quantity())) {

                        RejectedOrderSide rejectedSide = RejectedOrderSide.valueOf(side.name());

                        String reason;

                        if (position == null) {
                                reason = "No position for symbol";
                        } else {
                                reason = "Insufficient position quantity";
                        }

                        order.setStatus(OrderStatus.REJECTED);
                        order.setUpdatedAt(Instant.now());
                        order.setRejectionReason(reason);
                        orderRepository.save(order);

                        OrderRejected eventRejected = OrderRejected.newBuilder()
                                        .setEventId(UUID.randomUUID().toString())
                                        .setOrderId(orderId.toString())
                                        .setUserId(request.userId().toString())
                                        .setSymbol(symbol)
                                        .setSide(rejectedSide)
                                        .setReason(reason)
                                        .setQuantity(request.quantity())
                                        .setTimestamp(Instant.now().toString())
                                        .build();

                        orderEventProducer.publishRejected(eventRejected);
                        return;

                }

                if (tradeSide == TradeSide.BUY && totalPriceOrder.compareTo(cashBalance) > 0)

                {

                        RejectedOrderSide rejectedSide = RejectedOrderSide.valueOf(side.name());

                        String reason = "Insufficient cash balance";

                        order.setStatus(OrderStatus.REJECTED);
                        order.setUpdatedAt(Instant.now());
                        order.setRejectionReason(reason);
                        orderRepository.save(order);

                        OrderRejected eventRejected = OrderRejected.newBuilder()
                                        .setEventId(UUID.randomUUID().toString())
                                        .setOrderId(orderId.toString())
                                        .setUserId(request.userId().toString())
                                        .setSymbol(symbol)
                                        .setSide(rejectedSide)
                                        .setReason(reason)
                                        .setQuantity(request.quantity())
                                        .setTimestamp(Instant.now().toString())
                                        .build();

                        orderEventProducer.publishRejected(eventRejected);
                        return;

                }

                order.setPrice(price);
                order.setUpdatedAt(Instant.now());
                order.setStatus(OrderStatus.EXECUTED);
                orderRepository.save(order);

                OrderExecuted eventExecution = OrderExecuted.newBuilder()
                                .setEventId(UUID.randomUUID().toString())
                                .setOrderId(orderId.toString())
                                .setUserId(request.userId().toString())
                                .setSymbol(symbol)
                                .setSide(executedSide)
                                .setQuantity(request.quantity())
                                .setPrice(price)
                                .setTimestamp(Instant.now().toString())
                                .build();

                orderEventProducer.publishExecuted(eventExecution);
        }

        public List<OrderResponse> getOrdersByUserId(UUID userId) {
                List<Order> orders = orderRepository
                                .findByUserIdOrderByCreatedAtDesc(userId);

                return orders.stream()
                                .map(order -> new OrderResponse(order.getOrderId(),
                                                order.getSymbol(),
                                                order.getSide(),
                                                order.getQuantity(),
                                                order.getStatus(),
                                                order.getRejectionReason(),
                                                order.getPrice(),
                                                order.getCreatedAt(),
                                                order.getUpdatedAt()))
                                .toList();
        }
}
