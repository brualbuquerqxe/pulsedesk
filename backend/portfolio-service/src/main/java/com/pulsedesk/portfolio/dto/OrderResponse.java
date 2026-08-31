package com.pulsedesk.portfolio.dto;

import java.math.BigDecimal;
import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OrderResponse(

        String symbol,

        String side,

        long quantity,

        String status,

        BigDecimal price,

        Instant updatedAt

) {
}
