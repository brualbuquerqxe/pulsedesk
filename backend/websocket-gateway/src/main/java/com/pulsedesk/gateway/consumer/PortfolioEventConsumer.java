package com.pulsedesk.gateway.consumer;

import java.util.UUID;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import com.pulsedesk.contracts.events.PortfolioUpdated;
import com.pulsedesk.gateway.dto.PortfolioWebSocketMessage;

@Component
public class PortfolioEventConsumer {

    private final SimpMessagingTemplate simpMessagingTemplate;

    public PortfolioEventConsumer(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @KafkaListener(topics = "portfolio.updated", groupId = "websocket-gateway")
    public void consume(PortfolioUpdated event) {

        PortfolioWebSocketMessage message = new PortfolioWebSocketMessage(
                UUID.fromString(event.getPortfolioId().toString()),
                UUID.fromString(event.getUserId().toString()),
                event.getSymbol().toString(),
                event.getQuantity(),
                event.getAveragePrice(),
                event.getLastPrice(),
                event.getCashBalance(),
                event.getTimestamp().toString());

        simpMessagingTemplate.convertAndSend(
                "/topic/portfolio",
                message);
    }
}
