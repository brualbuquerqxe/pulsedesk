package com.pulsedesk.portfolio.controller;

import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.pulsedesk.portfolio.dto.PortfolioResponse;
import com.pulsedesk.portfolio.service.PortfolioService;

// Diz ao Spring que essa classe vai receber requisições HTTP e devolver dados como resposta
@RestController
public class PortfolioController {

    private final PortfolioService portfolioService;

    public PortfolioController(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    @GetMapping("/api/portfolio/{userId}")
    public PortfolioResponse getPortfolio(
            @PathVariable UUID userId) {

        return portfolioService
                .getPortfolioResponseByUserId(userId);
    }

}
