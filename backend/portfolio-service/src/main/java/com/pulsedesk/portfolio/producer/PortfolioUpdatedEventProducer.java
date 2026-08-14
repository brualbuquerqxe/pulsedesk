package com.pulsedesk.portfolio.producer;

import java.time.Instant;
import java.util.UUID;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.pulsedesk.contracts.events.PortfolioUpdated;
import com.pulsedesk.portfolio.entity.Portfolio;
import com.pulsedesk.portfolio.entity.Position;

@Component
public class PortfolioUpdatedEventProducer {

    private final KafkaTemplate<String, PortfolioUpdated> kafkaTemplate;

    public PortfolioUpdatedEventProducer(
            KafkaTemplate<String, PortfolioUpdated> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(Portfolio portfolio, Position position) {

        PortfolioUpdated event = PortfolioUpdated.newBuilder()
                .setEventId(UUID.randomUUID().toString())
                .setPortfolioId(portfolio.getId().toString())
                .setUserId(portfolio.getUser().getId().toString())
                .setSymbol(position.getSymbol())
                .setQuantity(position.getQuantity())
                .setAveragePrice(position.getAveragePrice())
                .setLastPrice(position.getLastPrice())
                .setCashBalance(portfolio.getCashBalance())
                .setTimestamp(Instant.now().toString())
                .build();

        kafkaTemplate.send(
                "portfolio.updated",
                portfolio.getId().toString(),
                event);
    }
}
