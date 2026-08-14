package com.pulsedesk.gateway.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import com.pulsedesk.contracts.events.MarketDataUpdated;
import com.pulsedesk.gateway.dto.MarketDataWebSocketMessage;

@Component
public class MarketDataEventConsumer {

    private final SimpMessagingTemplate simpMessagingTemplate;

    public MarketDataEventConsumer(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @KafkaListener(topics = "market-data.updated", groupId = "websocket-gateway")
    public void consume(MarketDataUpdated event) {

        MarketDataWebSocketMessage message = new MarketDataWebSocketMessage(
                event.getSymbol().toString(),
                event.getPrice(),
                event.getPercentageChange(),
                event.getTimestamp());

        simpMessagingTemplate.convertAndSend(
                "/topic/market-data",
                message);
    }
}
