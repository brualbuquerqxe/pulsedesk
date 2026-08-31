package com.pulsedesk.portfolio.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pulsedesk.portfolio.entity.DailyPortfolioSnapshot;

public interface DailyPortfolioSnapshotRepository
        extends JpaRepository<DailyPortfolioSnapshot, UUID> {

    List<DailyPortfolioSnapshot>
            findByPortfolioIdOrderBySnapshotDateAsc(UUID portfolioId);

    Optional<DailyPortfolioSnapshot>
            findByPortfolioIdAndSnapshotDate(
                    UUID portfolioId,
                    LocalDate snapshotDate);
}
