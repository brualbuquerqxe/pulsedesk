package com.pulsedesk.trading.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateOrderRequest(

        @NotNull
        UUID userId,

        @NotBlank
        String symbol,

        @NotBlank
        String side,

        @Positive
        long quantity

) {
}
