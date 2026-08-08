package com.pulsedesk.portfolio.entity;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity // Representa uma entidade persistida no banco de dados
@Table(name = "app_users", schema = "portfolio") // Qual tabela corresponde à classe
public class AppUser {

    @Id // Marca qual é a chave primária
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "display_name", nullable = false)
    private String displayName;

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private Instant createdAt;

    // Para o JPA conseguir instanciar a entidade (lê da tabela)
    protected AppUser() {
    }

    // Usado pela aplicação quando quiser criar um novo usuário
    public AppUser(String displayName) {
        this.displayName = displayName;
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

    public String getDisplayName() {
        return displayName;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    // Define o nome
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

}
