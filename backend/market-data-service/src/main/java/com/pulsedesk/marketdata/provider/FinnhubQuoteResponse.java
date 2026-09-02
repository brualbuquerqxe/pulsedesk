package com.pulsedesk.marketdata.provider;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FinnhubQuoteResponse {

    @JsonProperty("c")
    private BigDecimal currentPrice;

    @JsonProperty("dp")
    private double percentageChange;

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
