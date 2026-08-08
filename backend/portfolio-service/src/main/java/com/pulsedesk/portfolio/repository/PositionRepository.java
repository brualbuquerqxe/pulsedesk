package com.pulsedesk.portfolio.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pulsedesk.portfolio.entity.Position;

public interface PositionRepository
        extends JpaRepository<Position, UUID> {

    List<Position> findByPortfolioId(UUID portfolioId);
}
