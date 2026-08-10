package com.pulsedesk.marketdata.provider;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.pulsedesk.marketdata.dto.MarketDataResponse;

@SpringBootTest
class FinnhubMarketDataProviderIntegrationTest {

    @Autowired
    private ExternalMarketDataProvider marketDataProvider;

    @Test
    void shouldFetchQuoteFromFinnhub() {

        MarketDataResponse response = marketDataProvider.fetchQuote("AAPL");

        assertNotNull(response);
        assertEquals("AAPL", response.getSymbol());
        assertNotNull(response.getPrice());
        assertNotNull(response.getTimestamp());
    }
}
