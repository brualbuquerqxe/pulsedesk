package com.pulsedesk.portfolio.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;

import com.pulsedesk.portfolio.dto.PortfolioHistoryPointResponse;

import com.pulsedesk.portfolio.service.PortfolioHistoryService;

@RestController
@RequestMapping("/api/portfolio")
public class PortfolioHistoryController {

    private final PortfolioHistoryService portfolioHistoryService;

    public PortfolioHistoryController(
            PortfolioHistoryService portfolioHistoryService) {

        this.portfolioHistoryService = portfolioHistoryService;
    }

    @PostMapping("/{userId}/history/reconstruct")
    public ResponseEntity<Void> reconstructHistory(
            @PathVariable UUID userId) {

        portfolioHistoryService.reconstructPortfolioHistory(userId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{userId}/history")
    public ResponseEntity<List<PortfolioHistoryPointResponse>> getHistory(
            @PathVariable UUID userId) {

        List<PortfolioHistoryPointResponse> history = portfolioHistoryService.getPortfolioHistory(userId);

        return ResponseEntity.ok(history);
    }
}
