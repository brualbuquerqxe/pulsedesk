package com.pulsedesk.marketdata.producer;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import java.util.UUID;

import com.pulsedesk.marketdata.dto.MarketDataResponse;
import com.pulsedesk.contracts.events.MarketDataUpdated;

@Component
public class MarketDataEventProducer {

    private final KafkaTemplate<String, MarketDataUpdated> kafkaTemplate;

    public MarketDataEventProducer(
            KafkaTemplate<String, MarketDataUpdated> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(MarketDataResponse marketData) {

        MarketDataUpdated event = MarketDataUpdated.newBuilder()
                .setEventId(UUID.randomUUID().toString())
                .setSymbol(marketData.getSymbol())
                .setPrice(marketData.getPrice())
                .setPercentageChange(marketData.getPercentageChange())
                .setTimestamp(marketData.getTimestamp())
                .build();

        kafkaTemplate.send(
                "market-data.updated",
                marketData.getSymbol(),
                event);
    }
}
