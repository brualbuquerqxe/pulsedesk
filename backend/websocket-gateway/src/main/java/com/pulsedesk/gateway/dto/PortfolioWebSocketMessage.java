package com.pulsedesk.gateway.dto;

import java.math.BigDecimal;
import java.util.UUID;

public class PortfolioWebSocketMessage {

    private UUID portfolioId;
    private UUID userId;
    private String symbol;
    private int quantity;
    private BigDecimal averagePrice;
    private BigDecimal lastPrice;
    private BigDecimal cashBalance;
    private String timestamp;

    public PortfolioWebSocketMessage(
            UUID portfolioId,
            UUID userId,
            String symbol,
            int quantity,
            BigDecimal averagePrice,
            BigDecimal lastPrice,
            BigDecimal cashBalance,
            String timestamp) {

        this.portfolioId = portfolioId;
        this.userId = userId;
        this.symbol = symbol;
        this.quantity = quantity;
        this.averagePrice = averagePrice;
        this.lastPrice = lastPrice;
        this.cashBalance = cashBalance;
        this.timestamp = timestamp;
    }

    public UUID getPortfolioId() {
        return portfolioId;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getSymbol() {
        return symbol;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getAveragePrice() {
        return averagePrice;
    }

    public BigDecimal getLastPrice() {
        return lastPrice;
    }

    public BigDecimal getCashBalance() {
        return cashBalance;
    }

    public String getTimestamp() {
        return timestamp;
    }
}
