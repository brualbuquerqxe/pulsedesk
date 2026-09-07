package com.pulsedesk.portfolio.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.pulsedesk.contracts.events.MarketDataUpdated;
import com.pulsedesk.portfolio.service.PortfolioService;

@Component
public class MarketDataEventConsumer {

    private final PortfolioService portfolioService;

    public MarketDataEventConsumer(
            PortfolioService portfolioService
    ) {
        this.portfolioService = portfolioService;
    }

    @KafkaListener(
            topics = "market-data.updated",
            groupId = "portfolio-service"
    )
    public void consume(MarketDataUpdated event) {

        portfolioService.updateMarketPrice(
                event.getSymbol().toString(),
                event.getPrice()
        );
    }
}
