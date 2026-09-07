package com.pulsedesk.marketdata.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.pulsedesk.contracts.events.PortfolioUpdated;
import com.pulsedesk.marketdata.service.MarketDataService;

@Component
public class PortfolioUpdatedEventConsumer {

    private final MarketDataService marketDataService;

    public PortfolioUpdatedEventConsumer(
            MarketDataService marketDataService) {

        this.marketDataService = marketDataService;
    }

    @KafkaListener(
            topics = "portfolio.updated",
            groupId = "market-data-service"
    )
    public void consume(PortfolioUpdated event) {

        if (event.getQuantity() > 0) {

            marketDataService.monitorSymbol(
                    event.getSymbol().toString()
            );
        }
    }
}
