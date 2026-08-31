package com.pulsedesk.trading.service;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.springframework.stereotype.Service;

@Service
public class MarketHoursService {

    private static final ZoneId NEW_YORK =
            ZoneId.of("America/New_York");

    private static final LocalTime MARKET_OPEN =
            LocalTime.of(9, 30);

    private static final LocalTime MARKET_CLOSE =
            LocalTime.of(16, 0);

    public boolean isMarketOpen() {

        ZonedDateTime now =
                ZonedDateTime.now(NEW_YORK);

        DayOfWeek day = now.getDayOfWeek();

        if (day == DayOfWeek.SATURDAY ||
            day == DayOfWeek.SUNDAY) {
            return false;
        }

        LocalTime time = now.toLocalTime();

        return !time.isBefore(MARKET_OPEN)
                && time.isBefore(MARKET_CLOSE);
    }
}
