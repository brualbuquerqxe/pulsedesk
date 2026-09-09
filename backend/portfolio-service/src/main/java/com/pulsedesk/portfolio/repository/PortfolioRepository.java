package com.pulsedesk.portfolio.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pulsedesk.portfolio.entity.Portfolio;

public interface PortfolioRepository
        extends JpaRepository<Portfolio, UUID> {

    Optional<Portfolio> findByUserId(UUID userId);
}
