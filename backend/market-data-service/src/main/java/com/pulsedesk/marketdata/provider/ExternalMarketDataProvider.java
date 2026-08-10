package com.pulsedesk.marketdata.provider;
import com.pulsedesk.marketdata.dto.MarketDataResponse;

public interface ExternalMarketDataProvider {
    MarketDataResponse fetchQuote(String symbol);
}
