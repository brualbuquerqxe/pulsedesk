package com.pulsedesk.trading.client;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.pulsedesk.trading.dto.MarketDataResponse;

@Component
public class MarketDataClient {

    private final RestClient restClient;

    public MarketDataClient(RestClient.Builder builder) {
        this.restClient = builder
                .baseUrl("http://market-data-service:8081")
                .build();
    }

    public MarketDataResponse getMarketData(String symbol) {

        return restClient.get()
                .uri("/api/market-data/{symbol}", symbol)
                .retrieve()
                .body(MarketDataResponse.class);
    }
}
