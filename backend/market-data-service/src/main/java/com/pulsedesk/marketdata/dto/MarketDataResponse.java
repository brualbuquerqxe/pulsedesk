package com.pulsedesk.marketdata.dto;

import java.math.BigDecimal;
import java.time.Instant;

public class MarketDataResponse {

    private String symbol;
    private BigDecimal price;
    private Instant timestamp;
    private double percentageChange;

    public MarketDataResponse(String symbol, BigDecimal price, double percentageChange, Instant timestamp) {
        this.symbol = symbol;
        this.price = price;
        this.percentageChange = percentageChange;
        this.timestamp = timestamp;
    }

    public String getSymbol() {
        return symbol;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public double getPercentageChange() {
        return percentageChange;
    }

    public Instant getTimestamp() {
        return timestamp;
    }
}
