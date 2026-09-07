package com.pulsedesk.marketdata.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import com.pulsedesk.marketdata.client.PortfolioClient;
import com.pulsedesk.marketdata.dto.HistoricalPriceResponse;
import com.pulsedesk.marketdata.dto.MarketDataResponse;
import com.pulsedesk.marketdata.producer.HistoricalMarketDataEventProducer;
import com.pulsedesk.marketdata.producer.MarketDataEventProducer;
import com.pulsedesk.marketdata.provider.AlphaVantageHistoricalMarketDataProvider;
import com.pulsedesk.marketdata.provider.ExternalMarketDataProvider;

@Service
public class MarketDataService {

    private final ExternalMarketDataProvider marketDataProvider;
    private final AlphaVantageHistoricalMarketDataProvider alphaVantageHistoricalMarketDataProvider;

    private final MarketDataEventProducer eventProducer;
    private final HistoricalMarketDataEventProducer historicalEventProducer;

    private final Set<String> monitoredSymbols = ConcurrentHashMap.newKeySet();

    private final PortfolioClient portfolioClient;

    public MarketDataService(
            ExternalMarketDataProvider marketDataProvider,
            AlphaVantageHistoricalMarketDataProvider alphaVantageHistoricalMarketDataProvider,
            MarketDataEventProducer eventProducer,
            HistoricalMarketDataEventProducer historicalEventProducer,
            PortfolioClient portfolioClient) {

        this.marketDataProvider = marketDataProvider;
        this.alphaVantageHistoricalMarketDataProvider = alphaVantageHistoricalMarketDataProvider;
        this.eventProducer = eventProducer;
        this.historicalEventProducer = historicalEventProducer;
        this.portfolioClient = portfolioClient;
    }

    public MarketDataResponse getQuote(String symbol) {

        String normalizedSymbol = normalizeSymbol(symbol);

        publishHistoricalData(normalizedSymbol);

        return fetchAndPublish(normalizedSymbol);
    }

    public List<HistoricalPriceResponse> getHistoricalPrices(String symbol) {

        return alphaVantageHistoricalMarketDataProvider.getDailyCloseHistory(symbol);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void loadActiveSymbols() {
        synchronizeMonitoredSymbols();
    }

    public void monitorSymbol(String symbol) {

        String normalizedSymbol = normalizeSymbol(symbol);

        boolean added = monitoredSymbols.add(normalizedSymbol);

        if (added) {
            System.out.println(
                    "Started monitoring symbol: "
                            + normalizedSymbol);
        }
    }

    public void synchronizeMonitoredSymbols() {

        try {

            List<String> activeSymbols = portfolioClient.getActiveSymbols();

            Set<String> normalizedActiveSymbols = ConcurrentHashMap.newKeySet();

            for (String symbol : activeSymbols) {
                normalizedActiveSymbols.add(
                        normalizeSymbol(symbol));
            }

            monitoredSymbols.retainAll(
                    normalizedActiveSymbols);

            monitoredSymbols.addAll(
                    normalizedActiveSymbols);

            System.out.println(
                    "Synchronized monitored symbols: "
                            + monitoredSymbols);

        } catch (RestClientException | IllegalArgumentException exception) {

            System.out.println(
                    "Could not synchronize active symbols "
                            + "from portfolio service.");
        }
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

    @Scheduled(fixedDelayString = "${market-data.refresh-ms:10000}")
    public void refreshMonitoredSymbols() {

        for (String symbol : monitoredSymbols) {
            fetchAndPublish(symbol);
        }
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
