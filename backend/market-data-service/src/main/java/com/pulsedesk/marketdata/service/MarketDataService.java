package com.pulsedesk.marketdata.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(MarketDataService.class);

    private final ExternalMarketDataProvider marketDataProvider;
    private final AlphaVantageHistoricalMarketDataProvider historicalMarketDataProvider;

    private final MarketDataEventProducer eventProducer;
    private final HistoricalMarketDataEventProducer historicalEventProducer;

    private final PortfolioClient portfolioClient;

    private final Set<String> monitoredSymbols = ConcurrentHashMap.newKeySet();

    private final Map<String, LocalDate> lastPublishedHistoricalDate = new ConcurrentHashMap<>();

    private final Map<String, Object> historicalPublicationLocks = new ConcurrentHashMap<>();

    public MarketDataService(
            ExternalMarketDataProvider marketDataProvider,
            AlphaVantageHistoricalMarketDataProvider historicalMarketDataProvider,
            MarketDataEventProducer eventProducer,
            HistoricalMarketDataEventProducer historicalEventProducer,
            PortfolioClient portfolioClient) {

        this.marketDataProvider = marketDataProvider;
        this.historicalMarketDataProvider = historicalMarketDataProvider;
        this.eventProducer = eventProducer;
        this.historicalEventProducer = historicalEventProducer;
        this.portfolioClient = portfolioClient;
    }

    public MarketDataResponse getQuote(String symbol) {

        String normalizedSymbol = normalizeSymbol(symbol);

        MarketDataResponse response = fetchAndPublish(normalizedSymbol);

        try {

            publishHistoricalData(normalizedSymbol);

        } catch (RuntimeException exception) {

            logger.warn(
                    "Could not publish historical data for {}. Quote was returned successfully.",
                    normalizedSymbol,
                    exception);
        }

        return response;
    }

    public List<HistoricalPriceResponse> getHistoricalPrices(
            String symbol) {

        String normalizedSymbol = normalizeSymbol(symbol);

        return historicalMarketDataProvider
                .getDailyCloseHistory(normalizedSymbol);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void loadActiveSymbols() {

        synchronizeMonitoredSymbols();
    }

    public void monitorSymbol(String symbol) {

        String normalizedSymbol = normalizeSymbol(symbol);

        boolean added = monitoredSymbols.add(normalizedSymbol);

        if (added) {

            logger.info(
                    "Started monitoring symbol: {}",
                    normalizedSymbol);
        }
    }

    @Scheduled(fixedDelayString = "${market-data.portfolio-sync-ms:60000}")
    public void synchronizeMonitoredSymbols() {

        try {

            List<String> activeSymbols = portfolioClient.getActiveSymbols();

            if (activeSymbols == null) {

                logger.warn(
                        "Portfolio Service returned null active-symbol list");

                return;
            }

            Set<String> normalizedActiveSymbols = ConcurrentHashMap.newKeySet();

            for (String symbol : activeSymbols) {

                try {

                    normalizedActiveSymbols.add(
                            normalizeSymbol(symbol));

                } catch (IllegalArgumentException exception) {

                    logger.warn(
                            "Ignoring invalid symbol returned by Portfolio Service: {}",
                            symbol);
                }
            }

            monitoredSymbols.retainAll(
                    normalizedActiveSymbols);

            monitoredSymbols.addAll(
                    normalizedActiveSymbols);

            logger.info(
                    "Synchronized monitored symbols: {}",
                    monitoredSymbols);

        } catch (RestClientException exception) {

            logger.warn(
                    "Could not synchronize active symbols from Portfolio Service: {}",
                    exception.getMessage());
        }
    }

    @Scheduled(cron = "${market-data.historical-refresh-cron:0 15 18 * * MON-FRI}", zone = "America/New_York")
    public void refreshHistoricalData() {

        logger.info(
                "Starting historical-data refresh for {} monitored symbols",
                monitoredSymbols.size());

        for (String symbol : monitoredSymbols) {

            try {

                publishHistoricalData(
                        symbol,
                        true);

            } catch (RuntimeException exception) {

                logger.warn(
                        "Could not refresh historical data for {}: {}",
                        symbol,
                        exception.getMessage());
            }
        }
    }

    private void publishHistoricalData(
            String symbol) {

        publishHistoricalData(
                symbol,
                false);
    }

    private void publishHistoricalData(
            String symbol,
            boolean forceRefresh) {

        Object lock = historicalPublicationLocks.computeIfAbsent(
                symbol,
                ignored -> new Object());

        synchronized (lock) {

            List<HistoricalPriceResponse> history;

            if (forceRefresh) {

                history = historicalMarketDataProvider
                        .refreshDailyCloseHistory(symbol);

            } else {

                history = historicalMarketDataProvider
                        .getDailyCloseHistory(symbol);
            }

            if (history.isEmpty()) {

                logger.warn(
                        "No historical prices available for {}",
                        symbol);

                return;
            }

            LocalDate latestHistoricalDate = history.stream()
                    .map(HistoricalPriceResponse::date)
                    .max(LocalDate::compareTo)
                    .orElseThrow();

            LocalDate lastPublishedDate = lastPublishedHistoricalDate
                    .get(symbol);

            if (latestHistoricalDate.equals(lastPublishedDate)) {

                logger.debug(
                        "Historical data for {} ending on {} has already been published",
                        symbol,
                        latestHistoricalDate);

                return;
            }

            List<BigDecimal> closingPrices = history.stream()
                    .map(HistoricalPriceResponse::closePrice)
                    .toList();

            historicalEventProducer.publish(
                    symbol,
                    closingPrices);

            lastPublishedHistoricalDate.put(
                    symbol,
                    latestHistoricalDate);

            logger.info(
                    "Published historical data for {} ending on {}",
                    symbol,
                    latestHistoricalDate);
        }
    }

    @Scheduled(fixedDelayString = "${market-data.refresh-ms:10000}")
    public void refreshMonitoredSymbols() {

        for (String symbol : monitoredSymbols) {

            try {

                fetchAndPublish(symbol);

            } catch (RuntimeException exception) {

                logger.warn(
                        "Could not refresh market data for {}: {}",
                        symbol,
                        exception.getMessage());
            }
        }
    }

    private MarketDataResponse fetchAndPublish(
            String symbol) {

        MarketDataResponse response = marketDataProvider
                .fetchQuote(symbol);

        try {

            eventProducer.publish(response);

        } catch (RuntimeException exception) {

            logger.error(
                    "Quote for {} was fetched, but market-data event could not be published",
                    symbol,
                    exception);
        }

        return response;
    }

    private String normalizeSymbol(String symbol) {

        if (symbol == null || symbol.isBlank()) {

            throw new IllegalArgumentException(
                    "Symbol cannot be null or blank");
        }

        String normalizedSymbol = symbol
                .strip()
                .toUpperCase();

        if (!normalizedSymbol.matches("[A-Z0-9.-]+")) {

            throw new IllegalArgumentException(
                    "Invalid symbol format");
        }

        return normalizedSymbol;
    }
}
