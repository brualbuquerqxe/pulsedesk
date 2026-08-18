package com.pulsedesk.trading.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record PortfolioResponse(
        UUID portfolioId,
        BigDecimal cashBalance,
        List<PositionResponse> positions
) {
}
