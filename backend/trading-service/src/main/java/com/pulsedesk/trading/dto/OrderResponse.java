package com.pulsedesk.trading.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import com.pulsedesk.trading.entity.OrderStatus;
import com.pulsedesk.trading.entity.TradeSide;

public record OrderResponse(
        UUID orderId,
        String symbol,
        TradeSide side,
        long quantity,
        OrderStatus status,
        String rejectionReason,
        BigDecimal price,
        Instant createdAt,
        Instant updatedAt
) {
}

