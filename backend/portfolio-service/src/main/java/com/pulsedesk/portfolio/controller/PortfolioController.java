package com.pulsedesk.portfolio.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pulsedesk.portfolio.dto.PortfolioResponse;
import com.pulsedesk.portfolio.service.PortfolioService;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/portfolio")
public class PortfolioController {

    private final PortfolioService portfolioService;

    public PortfolioController(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    @GetMapping("/{userId}")
    public PortfolioResponse getPortfolio(
            @PathVariable UUID userId) {

        return portfolioService
                .getPortfolioResponseByUserId(userId);
    }

    @GetMapping("/active-symbols")
    public List<String> getActiveSymbols() {
        return portfolioService.getActiveSymbols();
    }
}
