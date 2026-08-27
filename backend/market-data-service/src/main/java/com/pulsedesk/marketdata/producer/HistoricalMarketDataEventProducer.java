package com.pulsedesk.marketdata.producer;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.pulsedesk.contracts.events.HistoricalMarketDataUpdated;

@Component
public class HistoricalMarketDataEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public HistoricalMarketDataEventProducer(
            KafkaTemplate<String, Object> kafkaTemplate) {

        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(
            String symbol,
            List<BigDecimal> closingPrices) {

        HistoricalMarketDataUpdated event =
                HistoricalMarketDataUpdated.newBuilder()
                        .setEventId(UUID.randomUUID().toString())
                        .setSymbol(symbol)
                        .setClosingPrices(closingPrices)
                        .setTimestamp(Instant.now())
                        .build();

        kafkaTemplate.send(
                "historical-market-data.updated",
                symbol,
                event
        );
    }
}
