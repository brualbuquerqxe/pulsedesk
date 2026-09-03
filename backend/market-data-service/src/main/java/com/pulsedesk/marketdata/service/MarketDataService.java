package com.pulsedesk.marketdata.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

import com.pulsedesk.marketdata.dto.HistoricalPriceResponse;
import com.pulsedesk.marketdata.dto.MarketDataResponse;

import com.pulsedesk.marketdata.producer.HistoricalMarketDataEventProducer;
import com.pulsedesk.marketdata.producer.MarketDataEventProducer;

import com.pulsedesk.marketdata.provider.ExternalMarketDataProvider;
import com.pulsedesk.marketdata.provider.AlphaVantageHistoricalMarketDataProvider;

@Service
public class MarketDataService {

    private final ExternalMarketDataProvider marketDataProvider;
    private final AlphaVantageHistoricalMarketDataProvider alphaVantageHistoricalMarketDataProvider;

    private final MarketDataEventProducer eventProducer;
    private final HistoricalMarketDataEventProducer historicalEventProducer;

    public MarketDataService(
            ExternalMarketDataProvider marketDataProvider,
            AlphaVantageHistoricalMarketDataProvider alphaVantageHistoricalMarketDataProvider,
            MarketDataEventProducer eventProducer,
            HistoricalMarketDataEventProducer historicalEventProducer) {

        this.marketDataProvider = marketDataProvider;
        this.alphaVantageHistoricalMarketDataProvider = alphaVantageHistoricalMarketDataProvider;
        this.eventProducer = eventProducer;
        this.historicalEventProducer = historicalEventProducer;
    }

    public MarketDataResponse getQuote(String symbol) {

        String normalizedSymbol = normalizeSymbol(symbol);

        publishHistoricalData(normalizedSymbol);

        return fetchAndPublish(normalizedSymbol);
    }

    public List<HistoricalPriceResponse> getHistoricalPrices(String symbol) {

        return alphaVantageHistoricalMarketDataProvider.getDailyCloseHistory(symbol);
    }

    private void publishHistoricalData(String symbol) {

        List<BigDecimal> closingPrices = alphaVantageHistoricalMarketDataProvider
                .getDailyCloseHistory(symbol)
                .stream()
                .map(HistoricalPriceResponse::closePrice)
                .toList();

        historicalEventProducer.publish(
                symbol,
                closingPrices);
    }

    private MarketDataResponse fetchAndPublish(String symbol) {

        MarketDataResponse response = marketDataProvider.fetchQuote(symbol);

        eventProducer.publish(response);

        return response;
    }

    private String normalizeSymbol(String symbol) {

        if (symbol == null || symbol.isBlank()) {
            throw new IllegalArgumentException(
                    "Symbol cannot be null or blank");
        }

        String normalizedSymbol = symbol.strip().toUpperCase();

        if (!normalizedSymbol.matches("[A-Z0-9.-]+")) {
            throw new IllegalArgumentException(
                    "Invalid symbol format");
        }

        return normalizedSymbol;
    }
}
