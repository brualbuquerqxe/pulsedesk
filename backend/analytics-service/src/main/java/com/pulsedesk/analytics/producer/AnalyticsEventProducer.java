package com.pulsedesk.analytics.producer;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.pulsedesk.contracts.events.AnalyticsUpdated;

@Component
public class AnalyticsEventProducer {

    private final KafkaTemplate<String, AnalyticsUpdated> kafkaTemplate;

    public AnalyticsEventProducer(
        KafkaTemplate<String, AnalyticsUpdated> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(AnalyticsUpdated analyticsUpdated) {

        kafkaTemplate.send(
                "analytics.updated",
                analyticsUpdated.getSymbol().toString(),
                analyticsUpdated);
    }
}
