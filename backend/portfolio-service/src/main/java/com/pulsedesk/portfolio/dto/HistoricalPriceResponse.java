package com.pulsedesk.portfolio.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record HistoricalPriceResponse(
        LocalDate date,
        BigDecimal closePrice
) {
}
