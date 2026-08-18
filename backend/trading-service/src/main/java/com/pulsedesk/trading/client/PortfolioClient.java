package com.pulsedesk.trading.client;

import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.pulsedesk.trading.dto.PortfolioResponse;

@Component
public class PortfolioClient {

    private final RestClient restClient;

    public PortfolioClient(RestClient.Builder builder) {
        this.restClient = builder
                .baseUrl("http://portfolio-service:8083")
                .build();
    }

    public PortfolioResponse getPortfolioResponse(UUID userId) {

        return restClient.get()
                .uri("/api/portfolio/{userId}", userId)
                .retrieve()
                .body(PortfolioResponse.class);
    }
}
