package com.pulsedesk.gateway.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import com.pulsedesk.contracts.events.AnalyticsUpdated;
import com.pulsedesk.gateway.dto.AnalyticsWebSocketMessage;

@Component
public class AnalyticsEventConsumer {

    private final SimpMessagingTemplate messagingTemplate;

    public AnalyticsEventConsumer(
            SimpMessagingTemplate messagingTemplate) {

        this.messagingTemplate = messagingTemplate;
    }

    @KafkaListener(topics = "analytics.updated", groupId = "websocket-gateway")
    public void consume(AnalyticsUpdated event) {

        AnalyticsWebSocketMessage message = new AnalyticsWebSocketMessage(
                event.getSymbol().toString(),
                event.getIndicator().toString(),
                event.getValue(),
                event.getTimestamp());

        messagingTemplate.convertAndSend(
                "/topic/analytics",
                message);
    }
}
