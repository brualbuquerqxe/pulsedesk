package com.pulsedesk.portfolio.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import com.pulsedesk.portfolio.entity.Position;

public interface PositionRepository
        extends JpaRepository<Position, UUID> {

    List<Position> findByPortfolioId(UUID portfolioId);

    List<Position> findBySymbol(String symbol);

    Optional<Position> findByPortfolioIdAndSymbol(UUID portfolioId, String symbol);

    @Query("""
            SELECT DISTINCT p.symbol
            FROM Position p
            WHERE p.quantity > 0
            """)
    List<String> findDistinctActiveSymbols();
}
