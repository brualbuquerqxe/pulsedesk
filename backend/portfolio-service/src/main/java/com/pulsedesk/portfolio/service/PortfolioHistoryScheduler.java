package com.pulsedesk.portfolio.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.pulsedesk.portfolio.repository.PortfolioRepository;

@Component
public class PortfolioHistoryScheduler {

    private static final Logger logger = LoggerFactory.getLogger(PortfolioHistoryScheduler.class);

    private final PortfolioHistoryService portfolioHistoryService;
    private final PortfolioRepository portfolioRepository;

    public PortfolioHistoryScheduler(
            PortfolioHistoryService portfolioHistoryService,
            PortfolioRepository portfolioRepository) {

        this.portfolioHistoryService = portfolioHistoryService;
        this.portfolioRepository = portfolioRepository;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void reconstructHistoryOnStartup() {

        logger.info("Reconstructing portfolio histories on application startup");

        updatePortfolioHistories();
    }

    @Scheduled(cron = "0 30 17 * * MON-FRI", zone = "America/New_York")
    public void updatePortfolioHistories() {

        logger.info("Updating portfolio histories");

        portfolioRepository.findAll()
                .forEach(portfolio -> {

                    try {

                        portfolioHistoryService
                                .reconstructPortfolioHistory(
                                        portfolio.getUser().getId());

                    } catch (Exception e) {

                        logger.error(
                                "Failed to reconstruct portfolio history for user {}",
                                portfolio.getUser().getId(),
                                e);
                    }
                });
    }
}
