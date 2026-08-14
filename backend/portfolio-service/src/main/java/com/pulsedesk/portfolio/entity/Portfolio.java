package com.pulsedesk.portfolio.entity;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "portfolios", schema = "portfolio")
public class Portfolio {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @OneToOne // user não é uma coluna, mas, sim, relação com outra entidade
    @JoinColumn(name = "user_id", nullable = false) // Chave estrangeira para app_users.id
    private AppUser user;

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private Instant createdAt;

    // Quanto o usuário tem de saldo
    @Column(name = "cash_balance", nullable = false)
    private BigDecimal cashBalance;

    // Última atualização
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected Portfolio() {
    }

    // Quando cria um portfólio, só precisa saber de quem que é + $$
    public Portfolio(AppUser user, BigDecimal cashBalance) {
        this.user = user;
        this.cashBalance = cashBalance;

    }

    // Antes de adicionar a entidade no banco, cria ID
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

    public AppUser getUser() {
        return user;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public BigDecimal getCashBalance() {
        return cashBalance;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void debitCashBalance(BigDecimal debitCash) {
        this.cashBalance = this.cashBalance.subtract(debitCash);
    }

    public void creditCashBalance(BigDecimal creditCash) {
        this.cashBalance = this.cashBalance.add(creditCash);
    }
}
