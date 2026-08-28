package com.pulsedesk.analytics.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.pulsedesk.contracts.events.AnalyticsUpdated;
import com.pulsedesk.contracts.events.HistoricalMarketDataUpdated;

@Service
public class AnalyticsService {

    private static final int REQUIRED_PRICES = 21;

    public AnalyticsUpdated analyze(
            HistoricalMarketDataUpdated event) {

        String symbol = event.getSymbol().toString();

        List<BigDecimal> closingPrices = event.getClosingPrices();

        if (closingPrices.size() < REQUIRED_PRICES) {
            throw new IllegalArgumentException(
                    "At least 21 closing prices are required");
        }

        double volatility = calculateVolatility(closingPrices);

        BigDecimal volatilityValue = BigDecimal.valueOf(volatility)
                .setScale(6, RoundingMode.HALF_UP);

        return AnalyticsUpdated.newBuilder()
                .setEventId(UUID.randomUUID().toString())
                .setSourceEventId(event.getEventId().toString())
                .setSymbol(symbol)
                .setIndicator("VOLATILITY_20D")
                .setValue(volatilityValue)
                .setTimestamp(Instant.now())
                .build();
    }

    private double calculateVolatility(
            List<BigDecimal> prices) {

        double[] returns = new double[prices.size() - 1];

        for (int i = 1; i < prices.size(); i++) {

            double previousPrice = prices.get(i - 1).doubleValue();

            double currentPrice = prices.get(i).doubleValue();

            returns[i - 1] = ((currentPrice - previousPrice)
                    / previousPrice) * 100;
        }

        double mean = 0;

        for (double value : returns) {
            mean += value;
        }

        mean /= returns.length;

        double squaredDifferences = 0;

        for (double value : returns) {
            squaredDifferences += Math.pow(value - mean, 2);
        }

        double variance = squaredDifferences / (returns.length - 1);

        return Math.sqrt(variance);
    }
}
