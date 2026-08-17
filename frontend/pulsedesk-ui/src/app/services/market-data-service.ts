import { HttpClient } from '@angular/common/http';
import { inject, Service } from '@angular/core';
import { MarketDataResponse } from '../models/market-data-response';

@Service()
export class MarketDataService {
    private http = inject(HttpClient);

    getMarketData(symbol: string) {
        return this.http.get<MarketDataResponse>(
            `http://localhost:8081/api/market-data/${symbol}`
        );
    }
    
}
