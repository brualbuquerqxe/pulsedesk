package com.pulsedesk.portfolio.client;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.pulsedesk.portfolio.dto.HistoricalPriceResponse;

@Component
public class HistoricalMarketDataClient {

    private final RestClient restClient;

    public HistoricalMarketDataClient(RestClient.Builder builder) {

        this.restClient = builder
                .baseUrl("http://market-data-service:8081")
                .build();
    }

    public List<HistoricalPriceResponse> getHistoricalPrices(
            String symbol) {

        return restClient.get()
                .uri("/api/market-data/{symbol}/history", symbol)
                .retrieve()
                .body(
                        new ParameterizedTypeReference<
                                List<HistoricalPriceResponse>>() {
                        });
    }
}
