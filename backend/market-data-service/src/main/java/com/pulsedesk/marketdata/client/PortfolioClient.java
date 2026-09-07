package com.pulsedesk.marketdata.client;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class PortfolioClient {

    private final RestClient restClient;

    public PortfolioClient(
            @Value("${portfolio-service.base-url}")
            String portfolioServiceBaseUrl) {

        this.restClient = RestClient
                .builder()
                .baseUrl(portfolioServiceBaseUrl)
                .build();
    }

    public List<String> getActiveSymbols() {

        String[] symbols = restClient
                .get()
                .uri("/api/portfolio/active-symbols")
                .retrieve()
                .body(String[].class);

        if (symbols == null) {
            return List.of();
        }

        return Arrays.asList(symbols);
    }
}
