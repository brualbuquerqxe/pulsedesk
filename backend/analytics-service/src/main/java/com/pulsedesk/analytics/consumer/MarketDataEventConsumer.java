package com.pulsedesk.analytics.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.pulsedesk.analytics.producer.AnalyticsEventProducer;
import com.pulsedesk.analytics.service.AnalyticsService;
import com.pulsedesk.contracts.events.AnalyticsUpdated;
import com.pulsedesk.contracts.events.MarketDataUpdated;

@Component
public class MarketDataEventConsumer {

    private final AnalyticsService analyticsService;
    private final AnalyticsEventProducer analyticsEventProducer;

    public MarketDataEventConsumer(AnalyticsService analyticsService, AnalyticsEventProducer analyticsEventProducer) {
        this.analyticsService = analyticsService;
        this.analyticsEventProducer = analyticsEventProducer;
    }

    @KafkaListener(topics = "market-data.updated", groupId = "analytics-service")
    public void consume(MarketDataUpdated event) {

        AnalyticsUpdated analyticsUpdated = analyticsService.analyze(event);

        analyticsEventProducer.publish(analyticsUpdated);

    }

}
