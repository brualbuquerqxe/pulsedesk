package com.pulsedesk.marketdata.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public record HistoricalPrice(
        LocalDate date,
        BigDecimal closePrice
) {
}
