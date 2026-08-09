package com.pulsedesk.portfolio.dto;

import java.math.BigDecimal;

public class PositionResponse {

    private String symbol;
    private Integer quantity;
    private BigDecimal averagePrice;
    private BigDecimal lastPrice;

    public PositionResponse(String symbol, Integer quantity, BigDecimal averagePrice, BigDecimal lastPrice) {
        this.symbol = symbol;
        this.quantity = quantity;
        this.averagePrice = averagePrice;
        this.lastPrice = lastPrice;
    }

    public String getSymbol() {
        return symbol;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public BigDecimal getAveragePrice() {
        return averagePrice;
    }

    public BigDecimal getLastPrice() {
        return lastPrice;
    }

}
