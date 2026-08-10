package com.pulsedesk.marketdata.provider.dto;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

// Como o Finnhub me entrega os dados
@JsonIgnoreProperties(ignoreUnknown = true)
public class FinnhubQuoteResponse {

    // Só pq o Json do Finnhub usa 'c'
    @JsonProperty("c")
    private BigDecimal currentPrice;

    // Só pq o Json do Finnhub usa 'dp'
    @JsonProperty("dp")
    private double percentageChange;

    // Só pq o Json do Finnhub usa 't'
    @JsonProperty("t")
    private long timestamp;

    public BigDecimal getCurrentPrice() {
        return currentPrice;
    }

    public double getPercentageChange() {
        return percentageChange;
    }

    public long getTimestamp() {
        return timestamp;
    }

}
