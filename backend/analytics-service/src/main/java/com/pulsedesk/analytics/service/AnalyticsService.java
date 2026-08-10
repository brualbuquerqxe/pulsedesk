package com.pulsedesk.analytics.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.pulsedesk.contracts.events.AnalyticsUpdated;
import com.pulsedesk.contracts.events.MarketDataUpdated;

@Service
public class AnalyticsService {

    public AnalyticsUpdated analyze(MarketDataUpdated event) {
        return AnalyticsUpdated.newBuilder()
                .setEventId(UUID.randomUUID().toString())
                .setSourceEventId(event.getEventId())
                .setSymbol(event.getSymbol())
                .setIndicator("PERCENTAGE_CHANGE")
                .setValue(BigDecimal.valueOf(event.getPercentageChange()))
                .setTimestamp(Instant.now())
                .build();
    }
}
