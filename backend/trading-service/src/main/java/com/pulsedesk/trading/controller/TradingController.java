package com.pulsedesk.trading.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pulsedesk.trading.dto.CreateOrderRequest;
import com.pulsedesk.trading.service.TradingService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/orders")
public class TradingController {

    private final TradingService tradingService;

    public TradingController(TradingService tradingService) {
        this.tradingService = tradingService;
    }

    @PostMapping
    public ResponseEntity<Void> createOrder(
            @Valid @RequestBody CreateOrderRequest request) {

        tradingService.createOrder(request);

        return ResponseEntity.accepted().build();
    }
}
