package com.pulsedesk.marketdata.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record HistoricalPriceResponse(
        LocalDate date,
        BigDecimal closePrice
) {
}
