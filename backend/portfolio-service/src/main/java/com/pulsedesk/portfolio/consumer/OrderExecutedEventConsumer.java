package com.pulsedesk.portfolio.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import com.pulsedesk.contracts.events.OrderExecuted;
import com.pulsedesk.portfolio.service.PortfolioService;

@Component
public class OrderExecutedEventConsumer {

    private final PortfolioService portfolioService;

    public OrderExecutedEventConsumer(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    @KafkaListener(topics = "order.executed", groupId = "portfolio-service")
    public void consumerEvent(OrderExecuted event) {
        portfolioService.processExecutedOrder(event);
    }
}
