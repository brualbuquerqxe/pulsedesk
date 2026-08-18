package com.pulsedesk.trading.entity;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "orders", schema = "trading")
public class Order {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "order_id", nullable = false, unique = true)
    private UUID orderId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "symbol", nullable = false)
    private String symbol;

    @Enumerated(EnumType.STRING)
    @Column(name = "side", nullable = false)
    private TradeSide side;

    @Column(name = "quantity", nullable = false)
    private long quantity;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderStatus status;

    @Column(name = "price", precision = 18, scale = 6)
    private BigDecimal price;

    @Column(name = "rejection_reason")
    private String rejectionReason;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected Order() {

    }

    public Order(UUID id, UUID orderId, UUID userId, String symbol, TradeSide side, long quantity, OrderStatus status,
            BigDecimal price, Instant createdAt, Instant updatedAt) {

        this.id = id;
        this.orderId = orderId;
        this.userId = userId;
        this.symbol = symbol;
        this.side = side;
        this.quantity = quantity;
        this.status = status;
        this.price = price;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() {
        return id;
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

    public TradeSide getSide() {
        return side;
    }

    public long getQuantity() {
        return quantity;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
