package com.pulsedesk.analytics.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.pulsedesk.analytics.producer.AnalyticsEventProducer;
import com.pulsedesk.analytics.service.AnalyticsService;
import com.pulsedesk.contracts.events.AnalyticsUpdated;
import com.pulsedesk.contracts.events.HistoricalMarketDataUpdated;

@Component
public class HistoricalMarketDataEventConsumer {

    private final AnalyticsService analyticsService;
    private final AnalyticsEventProducer analyticsEventProducer;

    public HistoricalMarketDataEventConsumer(
            AnalyticsService analyticsService,
            AnalyticsEventProducer analyticsEventProducer) {

        this.analyticsService = analyticsService;
        this.analyticsEventProducer = analyticsEventProducer;
    }

    @KafkaListener(
            topics = "historical-market-data.updated",
            groupId = "analytics-service"
    )
    public void consume(HistoricalMarketDataUpdated event) {

        AnalyticsUpdated analyticsUpdated =
                analyticsService.analyze(event);

        analyticsEventProducer.publish(analyticsUpdated);
    }
}
