package com.pulsedesk.gateway.dto;

import java.math.BigDecimal;
import java.util.UUID;

public class OrderWebSocketMessage {

    private UUID orderId;
    private UUID userId;
    private String symbol;
    private String side;
    private long quantity;
    private String status;
    private BigDecimal price;
    private String rejectionReason;
    private String timestamp;

    public OrderWebSocketMessage(
            UUID orderId,
            UUID userId,
            String symbol,
            String side,
            String status,
            long quantity,
            BigDecimal price,
            String rejectionReason,
            String timestamp) {

        this.orderId = orderId;
        this.userId = userId;
        this.symbol = symbol;
        this.side = side;
        this.quantity = quantity;
        this.status = status;
        this.price = price;
        this.rejectionReason = rejectionReason;
        this.timestamp = timestamp;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getSide() {
        return side;
    }

    public long getQuantity() {
        return quantity;
    }

    public String getStatus() {
        return status;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public String getTimestamp() {
        return timestamp;
    }
}
