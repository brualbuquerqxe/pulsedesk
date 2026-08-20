package com.pulsedesk.gateway.dto;

import java.math.BigDecimal;
import java.time.Instant;

public class AnalyticsWebSocketMessage {

    private String symbol;
    private String indicator;
    private BigDecimal value;
    private Instant timestamp;

    public AnalyticsWebSocketMessage(
            String symbol,
            String indicator,
            BigDecimal value,
            Instant timestamp) {

        this.symbol = symbol;
        this.indicator = indicator;
        this.value = value;
        this.timestamp = timestamp;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getIndicator() {
        return indicator;
    }

    public BigDecimal getValue() {
        return value;
    }

    public Instant getTimestamp() {
        return timestamp;
    }
}
