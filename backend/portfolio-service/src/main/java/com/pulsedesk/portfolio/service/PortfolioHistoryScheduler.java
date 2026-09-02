package com.pulsedesk.portfolio.service;

import org.springframework.stereotype.Component;
import org.springframework.scheduling.annotation.Scheduled;

import com.pulsedesk.portfolio.repository.PortfolioRepository;

@Component
public class PortfolioHistoryScheduler {

    PortfolioHistoryService portfolioHistoryService;
    PortfolioRepository portfolioRepository;

    public PortfolioHistoryScheduler(PortfolioHistoryService portfolioHistoryService,
            PortfolioRepository portfolioRepository) {
        this.portfolioHistoryService = portfolioHistoryService;
        this.portfolioRepository = portfolioRepository;
    }

    @Scheduled(cron = "0 30 17 * * MON-FRI", zone = "America/New_York")
    public void updatePortfolioHistories() {

        portfolioRepository.findAll()
                .forEach(portfolio -> portfolioHistoryService
                        .reconstructPortfolioHistory(
                                portfolio.getUser().getId()));
    }

}
