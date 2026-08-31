package com.pulsedesk.portfolio.entity;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(
    name = "daily_portfolio_snapshots",
    schema = "portfolio"
)
public class DailyPortfolioSnapshot {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_id", nullable = false)
    private Portfolio portfolio;

    @Column(name = "snapshot_date", nullable = false)
    private LocalDate snapshotDate;

    @Column(name = "cash_balance", nullable = false)
    private BigDecimal cashBalance;

    @Column(name = "total_value", nullable = false)
    private BigDecimal totalValue;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected DailyPortfolioSnapshot() {
    }

    public DailyPortfolioSnapshot(
            UUID id,
            Portfolio portfolio,
            LocalDate snapshotDate,
            BigDecimal cashBalance,
            BigDecimal totalValue,
            Instant createdAt) {

        this.id = id;
        this.portfolio = portfolio;
        this.snapshotDate = snapshotDate;
        this.cashBalance = cashBalance;
        this.totalValue = totalValue;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public Portfolio getPortfolio() {
        return portfolio;
    }

    public LocalDate getSnapshotDate() {
        return snapshotDate;
    }

    public BigDecimal getCashBalance() {
        return cashBalance;
    }

    public BigDecimal getTotalValue() {
        return totalValue;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
