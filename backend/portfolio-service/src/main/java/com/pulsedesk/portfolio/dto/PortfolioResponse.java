package com.pulsedesk.portfolio.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

// Define o formato dos dados que serão enviados pela API pro frontend
public class PortfolioResponse {

    private UUID portfolioId;
    private BigDecimal cashBalance;
    private List<PositionResponse> positions;

    public PortfolioResponse(UUID portfolioId, BigDecimal cashBalance, List<PositionResponse> positions) {
        this.portfolioId = portfolioId;
        this.cashBalance = cashBalance;
        this.positions = positions;
    }

    public UUID getPortfolioId() {
        return portfolioId;
    }

    public BigDecimal getCashBalance() {
        return cashBalance;
    }

    public List<PositionResponse> getPositions() {
        return positions;
    }
}
