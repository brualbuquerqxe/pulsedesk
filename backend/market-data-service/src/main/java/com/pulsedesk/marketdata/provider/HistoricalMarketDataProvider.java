package com.pulsedesk.marketdata.provider;

import java.math.BigDecimal;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.pulsedesk.marketdata.model.HistoricalPrice;

@Component
public class HistoricalMarketDataProvider {

    private static final int REQUIRED_PRICES = 21;

    private final String apiKey;

    private final HttpClient httpClient;

    private final Map<String, List<HistoricalPrice>> cache =
            new ConcurrentHashMap<>();

    public HistoricalMarketDataProvider(
            @Value("${alpha-vantage.api-key}") String apiKey) {

        this.apiKey = apiKey;
        this.httpClient = HttpClient.newHttpClient();
    }

    public List<BigDecimal> getRecentDailyCloses(String symbol) {

        List<HistoricalPrice> history =
                getDailyCloseHistory(symbol);

        if (history.size() < REQUIRED_PRICES) {
            throw new IllegalStateException(
                    "Not enough historical data for " + symbol);
        }

        return history
                .subList(
                        history.size() - REQUIRED_PRICES,
                        history.size())
                .stream()
                .map(HistoricalPrice::closePrice)
                .toList();
    }

    public List<HistoricalPrice> getDailyCloseHistory(String symbol) {

        String normalizedSymbol =
                symbol.strip().toUpperCase();

        return cache.computeIfAbsent(
                normalizedSymbol,
                this::fetchFromAlphaVantage);
    }

    private List<HistoricalPrice> fetchFromAlphaVantage(String symbol) {

        String encodedSymbol =
                URLEncoder.encode(
                        symbol,
                        StandardCharsets.UTF_8);

        String encodedApiKey =
                URLEncoder.encode(
                        apiKey,
                        StandardCharsets.UTF_8);

        String url =
                "https://www.alphavantage.co/query"
                        + "?function=TIME_SERIES_DAILY"
                        + "&symbol=" + encodedSymbol
                        + "&outputsize=compact"
                        + "&datatype=csv"
                        + "&apikey=" + encodedApiKey;

        HttpRequest request =
                HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .GET()
                        .build();

        try {

            HttpResponse<String> response =
                    httpClient.send(
                            request,
                            HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new IllegalStateException(
                        "Alpha Vantage request failed. HTTP "
                                + response.statusCode());
            }

            String body = response.body().strip();

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
                                    HistoricalPrice::date))
                    .toList();

        } catch (InterruptedException exception) {

            Thread.currentThread().interrupt();

            throw new IllegalStateException(
                    "Alpha Vantage request was interrupted",
                    exception);

        } catch (Exception exception) {

            throw new IllegalStateException(
                    "Error fetching historical data for " + symbol,
                    exception);
        }
    }

    private HistoricalPrice parseHistoricalPrice(String line) {

        String[] values = line.split(",");

        LocalDate date =
                LocalDate.parse(values[0]);

        BigDecimal closePrice =
                new BigDecimal(values[4]);

        return new HistoricalPrice(
                date,
                closePrice);
    }
}
