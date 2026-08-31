package com.pulsedesk.portfolio.client;

import java.util.List;
import java.util.UUID;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.pulsedesk.portfolio.dto.OrderResponse;

@Component
public class TradingClient {

    private final RestClient restClient;

    public TradingClient(RestClient.Builder builder) {
        this.restClient = builder
                .baseUrl("http://trading-service:8082")
                .build();
    }

    public List<OrderResponse> getOrderResponses(UUID userId) {

        return restClient.get()
                .uri("/api/orders/{userId}", userId)
                .retrieve()
                .body(new ParameterizedTypeReference<List<OrderResponse>>() {});
    }
}
