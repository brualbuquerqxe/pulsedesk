package com.pulsedesk.marketdata.controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pulsedesk.marketdata.dto.MarketDataResponse;
import com.pulsedesk.marketdata.model.HistoricalPrice;
import com.pulsedesk.marketdata.service.MarketDataService;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/market-data")
public class MarketDataController {

    private final MarketDataService marketDataService;

    public MarketDataController(MarketDataService marketDataService) {
        this.marketDataService = marketDataService;
    }

    @GetMapping("/{symbol}")
    public MarketDataResponse getQuote(@PathVariable String symbol) {
        return marketDataService.getQuote(symbol);
    }

    @GetMapping("/{symbol}/history")
    public List<HistoricalPrice> getHistoricalPrices(
            @PathVariable String symbol) {

        return marketDataService.getHistoricalPrices(symbol);
    }
}
