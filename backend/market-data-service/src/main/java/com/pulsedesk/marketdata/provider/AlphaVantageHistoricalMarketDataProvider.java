package com.pulsedesk.marketdata.provider;

import java.math.BigDecimal;

import java.time.LocalDate;
import java.time.ZoneId;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.pulsedesk.marketdata.dto.HistoricalPriceResponse;

@Component
public class AlphaVantageHistoricalMarketDataProvider {

        private final String apiKey;

        private final RestClient restClient;

        private final Map<String, CachedHistory> cache =
                        new ConcurrentHashMap<>();

        public AlphaVantageHistoricalMarketDataProvider(
                        @Value("${alpha-vantage.api-key}") String apiKey) {

                this.apiKey = apiKey;

                this.restClient = RestClient.builder()
                                .baseUrl("https://www.alphavantage.co")
                                .build();
        }

        public List<HistoricalPriceResponse> getDailyCloseHistory(
                        String symbol) {

                String normalizedSymbol =
                                symbol.strip().toUpperCase();

                LocalDate today =
                                LocalDate.now(
                                                ZoneId.of("America/New_York"));

                CachedHistory cachedHistory =
                                cache.get(normalizedSymbol);

                if (cachedHistory != null
                                && cachedHistory.fetchedAt().equals(today)) {

                        return cachedHistory.prices();
                }

                List<HistoricalPriceResponse> history =
                                fetchFromAlphaVantage(normalizedSymbol);

                cache.put(
                                normalizedSymbol,
                                new CachedHistory(today, history));

                return history;
        }

        private List<HistoricalPriceResponse> fetchFromAlphaVantage(
                        String symbol) {

                try {

                        String body = restClient.get()
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

                        if (body == null) {
                                throw new IllegalStateException(
                                                "Alpha Vantage returned an empty response");
                        }

                        body = body.strip();

                        if (!body.startsWith("timestamp,")) {
                                throw new IllegalStateException(
                                                "Alpha Vantage did not return historical data");
                        }

                        return body.lines()
                                        .skip(1)
                                        .filter(line -> !line.isBlank())
                                        .map(this::parseHistoricalPrice)
                                        .sorted(
                                                        Comparator.comparing(
                                                                        HistoricalPriceResponse::date))
                                        .toList();

                } catch (Exception exception) {

                        throw new IllegalStateException(
                                        "Error fetching historical data for "
                                                        + symbol,
                                        exception);
                }
        }

        private HistoricalPriceResponse parseHistoricalPrice(
                        String line) {

                String[] values = line.split(",");

                LocalDate date =
                                LocalDate.parse(values[0]);

                BigDecimal closePrice =
                                new BigDecimal(values[4]);

                return new HistoricalPriceResponse(
                                date,
                                closePrice);
        }

        private record CachedHistory(
                        LocalDate fetchedAt,
                        List<HistoricalPriceResponse> prices) {
        }
}
