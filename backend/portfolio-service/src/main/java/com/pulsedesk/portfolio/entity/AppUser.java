package com.pulsedesk.portfolio.entity;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "app_users", schema = "portfolio")
public class AppUser {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "display_name", nullable = false)
    private String displayName;

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private Instant createdAt;

    protected AppUser() {
    }

    public AppUser(String displayName) {
        this.displayName = displayName;
    }

    @PrePersist
    private void newId() {
        if (id == null) {
            id = UUID.randomUUID();
        }
    }

    public UUID getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

}
