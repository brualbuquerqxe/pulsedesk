package com.pulsedesk.marketdata.provider;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import com.pulsedesk.marketdata.dto.HistoricalPriceResponse;

@Component
public class AlphaVantageHistoricalMarketDataProvider {

        private static final Logger logger = LoggerFactory.getLogger(
                        AlphaVantageHistoricalMarketDataProvider.class);

        private static final long MIN_REQUEST_INTERVAL_MS = 1100;

        private static final ZoneId MARKET_ZONE = ZoneId.of("America/New_York");

        private final String apiKey;

        private final RestClient restClient;

        private final Map<String, CachedHistory> cache = new ConcurrentHashMap<>();

        private final Map<String, Object> symbolLocks = new ConcurrentHashMap<>();

        private final Object rateLimitLock = new Object();

        private long lastRequestTimestamp = 0L;

        public AlphaVantageHistoricalMarketDataProvider(
                        @Value("${alpha-vantage.api-key}") String apiKey) {

                this.apiKey = apiKey;

                this.restClient = RestClient.builder()
                                .baseUrl("https://www.alphavantage.co")
                                .build();
        }

        public List<HistoricalPriceResponse> getDailyCloseHistory(
                        String symbol) {

                return getDailyCloseHistory(
                                symbol,
                                false);
        }

        public List<HistoricalPriceResponse> refreshDailyCloseHistory(
                        String symbol) {

                return getDailyCloseHistory(
                                symbol,
                                true);
        }

        private List<HistoricalPriceResponse> getDailyCloseHistory(
                        String symbol,
                        boolean forceRefresh) {

                String normalizedSymbol = normalizeSymbol(symbol);

                LocalDate today = LocalDate.now(MARKET_ZONE);

                CachedHistory cachedHistory = cache.get(normalizedSymbol);

                if (!forceRefresh
                                && isCurrentCache(
                                                cachedHistory,
                                                today)) {

                        logger.debug(
                                        "Using cached Alpha Vantage history for {}",
                                        normalizedSymbol);

                        return cachedHistory.prices();
                }

                Object symbolLock = symbolLocks.computeIfAbsent(
                                normalizedSymbol,
                                ignored -> new Object());

                synchronized (symbolLock) {

                        cachedHistory = cache.get(normalizedSymbol);

                        if (!forceRefresh
                                        && isCurrentCache(
                                                        cachedHistory,
                                                        today)) {

                                return cachedHistory.prices();
                        }

                        try {

                                List<HistoricalPriceResponse> history = fetchFromAlphaVantage(
                                                normalizedSymbol);

                                cache.put(
                                                normalizedSymbol,
                                                new CachedHistory(
                                                                today,
                                                                history));

                                return history;

                        } catch (RuntimeException exception) {

                                if (cachedHistory != null
                                                && !cachedHistory.prices().isEmpty()) {

                                        logger.warn(
                                                        "Alpha Vantage unavailable for {}. "
                                                                        + "Using cached historical data from {}.",
                                                        normalizedSymbol,
                                                        cachedHistory.fetchedAt());

                                        return cachedHistory.prices();
                                }

                                throw exception;
                        }
                }
        }

        private boolean isCurrentCache(
                        CachedHistory cachedHistory,
                        LocalDate today) {

                return cachedHistory != null
                                && cachedHistory.fetchedAt().equals(today)
                                && !cachedHistory.prices().isEmpty();
        }

        private List<HistoricalPriceResponse> fetchFromAlphaVantage(
                        String symbol) {

                waitForRequestSlot();

                final String body;

                try {

                        body = restClient.get()
                                        .uri(uriBuilder -> uriBuilder
                                                        .path("/query")
                                                        .queryParam(
                                                                        "function",
                                                                        "TIME_SERIES_DAILY")
                                                        .queryParam(
                                                                        "symbol",
                                                                        symbol)
                                                        .queryParam(
                                                                        "outputsize",
                                                                        "compact")
                                                        .queryParam(
                                                                        "datatype",
                                                                        "csv")
                                                        .queryParam(
                                                                        "apikey",
                                                                        apiKey)
                                                        .build())
                                        .retrieve()
                                        .body(String.class);

                } catch (RestClientException exception) {

                        throw new IllegalStateException(
                                        "HTTP error while fetching historical data for "
                                                        + symbol,
                                        exception);
                }

                if (body == null || body.isBlank()) {

                        throw new IllegalStateException(
                                        "Alpha Vantage returned an empty response for "
                                                        + symbol);
                }

                String normalizedBody = body.strip();

                if (!normalizedBody.startsWith("timestamp,")) {

                        if (isRateLimitResponse(normalizedBody)) {

                                logger.warn(
                                                "Alpha Vantage rate limit reached for {}",
                                                symbol);

                                throw new IllegalStateException(
                                                "Alpha Vantage rate limit reached");
                        }

                        if (isInvalidSymbolResponse(normalizedBody)) {

                                throw new IllegalArgumentException(
                                                "Alpha Vantage rejected symbol "
                                                                + symbol);
                        }

                        logger.error(
                                        "Unexpected Alpha Vantage response for {}: {}",
                                        symbol,
                                        shorten(normalizedBody));

                        throw new IllegalStateException(
                                        "Alpha Vantage returned an unexpected response");
                }

                List<HistoricalPriceResponse> history = normalizedBody.lines()
                                .skip(1)
                                .filter(line -> !line.isBlank())
                                .map(this::parseHistoricalPrice)
                                .sorted(
                                                Comparator.comparing(
                                                                HistoricalPriceResponse::date))
                                .toList();

                if (history.isEmpty()) {

                        throw new IllegalStateException(
                                        "Alpha Vantage returned no historical prices for "
                                                        + symbol);
                }

                logger.info(
                                "Fetched {} historical prices from Alpha Vantage for {}",
                                history.size(),
                                symbol);

                return history;
        }

        private void waitForRequestSlot() {

                synchronized (rateLimitLock) {

                        long now = System.currentTimeMillis();

                        long elapsed = now - lastRequestTimestamp;

                        long remainingDelay = MIN_REQUEST_INTERVAL_MS - elapsed;

                        if (remainingDelay > 0) {

                                try {

                                        Thread.sleep(remainingDelay);

                                } catch (InterruptedException exception) {

                                        Thread.currentThread().interrupt();

                                        throw new IllegalStateException(
                                                        "Interrupted while waiting for Alpha Vantage rate limit",
                                                        exception);
                                }
                        }

                        lastRequestTimestamp = System.currentTimeMillis();
                }
        }

        private boolean isRateLimitResponse(
                        String body) {

                return body.contains("\"Information\"")
                                || body.contains("\"Note\"");
        }

        private boolean isInvalidSymbolResponse(
                        String body) {

                return body.contains("\"Error Message\"");
        }

        private HistoricalPriceResponse parseHistoricalPrice(
                        String line) {

                String[] values = line.split(",");

                if (values.length < 5) {

                        throw new IllegalStateException(
                                        "Invalid Alpha Vantage CSV row: "
                                                        + line);
                }

                try {

                        LocalDate date = LocalDate.parse(
                                        values[0].trim());

                        BigDecimal closePrice = new BigDecimal(
                                        values[4].trim());

                        return new HistoricalPriceResponse(
                                        date,
                                        closePrice);

                } catch (RuntimeException exception) {

                        throw new IllegalStateException(
                                        "Could not parse Alpha Vantage CSV row: "
                                                        + line,
                                        exception);
                }
        }

        private String normalizeSymbol(
                        String symbol) {

                if (symbol == null || symbol.isBlank()) {

                        throw new IllegalArgumentException(
                                        "Symbol cannot be null or blank");
                }

                String normalizedSymbol = symbol
                                .strip()
                                .toUpperCase();

                if (!normalizedSymbol.matches("[A-Z0-9.-]+")) {

                        throw new IllegalArgumentException(
                                        "Invalid symbol format: "
                                                        + symbol);
                }

                return normalizedSymbol;
        }

        private String shorten(
                        String value) {

                int maximumLength = 500;

                if (value.length() <= maximumLength) {

                        return value;
                }

                return value.substring(
                                0,
                                maximumLength)
                                + "...";
        }

        private record CachedHistory(
                        LocalDate fetchedAt,
                        List<HistoricalPriceResponse> prices) {
        }
}
