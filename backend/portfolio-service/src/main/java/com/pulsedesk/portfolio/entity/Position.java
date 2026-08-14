package com.pulsedesk.portfolio.entity;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "positions", schema = "portfolio")
public class Position {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @ManyToOne // Muitas posições para apenas um portfolio
    @JoinColumn(name = "portfolio_id", nullable = false) // Chave estrangeira para portfolio.id
    private Portfolio portfolio;

    @Column(name = "symbol", nullable = false)
    private String symbol;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "average_price", nullable = false)
    private BigDecimal averagePrice;

    @Column(name = "last_price")
    private BigDecimal lastPrice;

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false, insertable = false)
    private Instant updatedAt;

    protected Position() {
    }

    public Position(Portfolio portfolio, String symbol, Integer quantity, BigDecimal averagePrice) {
        this.portfolio = portfolio;
        this.symbol = symbol;
        this.quantity = quantity;
        this.averagePrice = averagePrice;
    }

    // Antes de adicionar a entidade no banco, cria ID da posição
    @PrePersist
    private void newId() {
        if (id == null) {
            id = UUID.randomUUID();
        }
    }

    // Gets, já que é private
    public UUID getId() {
        return id;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public Portfolio getPortfolio() {
        return portfolio;
    }

    public String getSymbol() {
        return symbol;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public BigDecimal getAveragePrice() {
        return averagePrice;
    }

    public BigDecimal getLastPrice() {
        return lastPrice;
    }

    public void setLastPrice(BigDecimal lastPrice) {
        this.lastPrice = lastPrice;
    }

    public void addQuantity(Integer orderQuantity) {
        this.quantity = this.quantity + orderQuantity;
    }

    public void removeQuantity(Integer orderQuantity) {
        this.quantity = this.quantity - orderQuantity;
    }

    public void setAveragePrice(BigDecimal orderPrice, Integer orderQuantity) {

        BigDecimal currentQuantity = BigDecimal.valueOf(this.quantity);
        BigDecimal purchasedQuantity = BigDecimal.valueOf(orderQuantity);

        BigDecimal currentTotalValue = this.averagePrice.multiply(currentQuantity);

        BigDecimal purchasedTotalValue = orderPrice.multiply(purchasedQuantity);

        BigDecimal newQuantity = currentQuantity.add(purchasedQuantity);

        this.averagePrice = currentTotalValue
                .add(purchasedTotalValue)
                .divide(newQuantity, 6, RoundingMode.HALF_UP);
    }
}
