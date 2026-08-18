package com.pulsedesk.trading.dto;

import java.math.BigDecimal;

public record PositionResponse(
        String symbol,
        long quantity,
        BigDecimal averagePrice,
        BigDecimal lastPrice
) {
}
