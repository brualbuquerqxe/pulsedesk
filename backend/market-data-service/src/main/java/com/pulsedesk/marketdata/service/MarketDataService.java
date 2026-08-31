package com.pulsedesk.marketdata.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.pulsedesk.marketdata.dto.MarketDataResponse;
import com.pulsedesk.marketdata.model.HistoricalPrice;
import com.pulsedesk.marketdata.producer.HistoricalMarketDataEventProducer;
import com.pulsedesk.marketdata.producer.MarketDataEventProducer;
import com.pulsedesk.marketdata.provider.ExternalMarketDataProvider;
import com.pulsedesk.marketdata.provider.HistoricalMarketDataProvider;

@Service
public class MarketDataService {

    private final ExternalMarketDataProvider marketDataProvider;
    private final HistoricalMarketDataProvider historicalMarketDataProvider;

    private final MarketDataEventProducer eventProducer;
    private final HistoricalMarketDataEventProducer historicalEventProducer;

    private final Set<String> monitoredSymbols = ConcurrentHashMap.newKeySet();

    public MarketDataService(
            ExternalMarketDataProvider marketDataProvider,
            HistoricalMarketDataProvider historicalMarketDataProvider,
            MarketDataEventProducer eventProducer,
            HistoricalMarketDataEventProducer historicalEventProducer) {

        this.marketDataProvider = marketDataProvider;
        this.historicalMarketDataProvider = historicalMarketDataProvider;
        this.eventProducer = eventProducer;
        this.historicalEventProducer = historicalEventProducer;
    }

    public MarketDataResponse getQuote(String symbol) {

        String normalizedSymbol = normalizeSymbol(symbol);

        monitoredSymbols.add(normalizedSymbol);

        publishHistoricalData(normalizedSymbol);

        return fetchAndPublish(normalizedSymbol);
    }

    public List<HistoricalPrice> getHistoricalPrices(String symbol) {

        String normalizedSymbol = symbol.strip().toUpperCase();

        return historicalMarketDataProvider
                .getDailyCloseHistory(normalizedSymbol);
    }

    @Scheduled(fixedDelayString = "${market-data.refresh-ms:10000}")
    public void refreshMonitoredSymbols() {

        for (String symbol : monitoredSymbols) {
            fetchAndPublish(symbol);
        }
    }

    private void publishHistoricalData(String symbol) {

        List<BigDecimal> closingPrices = historicalMarketDataProvider
                .getRecentDailyCloses(symbol);

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
