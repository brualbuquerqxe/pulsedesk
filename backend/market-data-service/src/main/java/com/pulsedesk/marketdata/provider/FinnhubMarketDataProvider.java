package com.pulsedesk.marketdata.provider;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.time.Instant;

import com.pulsedesk.marketdata.dto.MarketDataResponse;

@Component
public class FinnhubMarketDataProvider implements ExternalMarketDataProvider {

        private final String apiKey;

        private final RestClient restClient;

        @Override
        public MarketDataResponse fetchQuote(String symbol) {

                FinnhubQuoteResponse response = restClient.get()
                                .uri(uriBuilder -> uriBuilder
                                                .path("/quote")
                                                .queryParam("symbol", symbol)
                                                .build())
                                .header("X-Finnhub-Token", apiKey)
                                .retrieve()
                                .body(FinnhubQuoteResponse.class);

                if (response == null || response.getCurrentPrice() == null
                                || response.getCurrentPrice().compareTo(BigDecimal.ZERO) <= 0
                                || response.getTimestamp() <= 0) {
                        throw new IllegalArgumentException(
                                        "No market data found for symbol: " + symbol);
                }

                Instant timestamp = Instant.ofEpochSecond(response.getTimestamp());

                return new MarketDataResponse(
                                symbol,
                                response.getCurrentPrice(),
                                response.getPercentageChange(),
                                timestamp);
        }

        public FinnhubMarketDataProvider(@Value("${finnhub.api-key}") String apiKey) {
                this.apiKey = apiKey;
                this.restClient = RestClient.builder()
                                .baseUrl("https://api.finnhub.io/api/v1")
                                .build();
        }
}
