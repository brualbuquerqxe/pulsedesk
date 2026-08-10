package com.pulsedesk.marketdata.service;

import org.springframework.stereotype.Service;

import com.pulsedesk.marketdata.dto.MarketDataResponse;
import com.pulsedesk.marketdata.producer.MarketDataEventProducer;
import com.pulsedesk.marketdata.provider.ExternalMarketDataProvider;

// Recebe um símbolo e pede a cotação para o provetodr (finnhub)
@Service
public class MarketDataService {

    private final ExternalMarketDataProvider marketDataProvider;

    private final MarketDataEventProducer eventProducer;

    public MarketDataService(ExternalMarketDataProvider marketDataProvider, MarketDataEventProducer eventProducer) {
        this.marketDataProvider = marketDataProvider;
        this.eventProducer = eventProducer;
    }

    public MarketDataResponse getQuote(String symbol) {

        if (symbol == null || symbol.isBlank()) {
            throw new IllegalArgumentException("Symbol cannot be null or blank");
        }

        String normalizedSymbol = symbol.strip().toUpperCase();

        if (!normalizedSymbol.matches("[A-Z0-9.-]+")) {
            throw new IllegalArgumentException("Invalid symbol format");
        }

        MarketDataResponse response = marketDataProvider.fetchQuote(normalizedSymbol);

        eventProducer.publish(response);

        return response;
    }
}
