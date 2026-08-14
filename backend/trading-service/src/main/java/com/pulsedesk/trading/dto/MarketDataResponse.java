package com.pulsedesk.trading.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record MarketDataResponse(
        String symbol,
        BigDecimal price,
        Double percentageChange,
        Instant timestamp
) {
}
