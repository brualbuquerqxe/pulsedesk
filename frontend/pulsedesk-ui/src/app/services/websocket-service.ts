import { isPlatformBrowser } from '@angular/common';
import { inject, PLATFORM_ID, Service } from '@angular/core';
import { Client } from '@stomp/stompjs';
import { Subject } from 'rxjs';
import { MarketDataWebSocketMessage } from '../models/market-data-websocket-message';
import { PortfolioWebSocketMessage } from '../models/portfolio-websocket-message';

@Service()
export class Websocket {

    private platformId = inject(PLATFORM_ID);

    private client = new Client({
        brokerURL: 'ws://localhost:8084/ws',
        reconnectDelay: 5000
    });

    private marketDataSubject = new Subject<MarketDataWebSocketMessage>();
    private portfolioSubject = new Subject<PortfolioWebSocketMessage>();

    portfolio$ = this.portfolioSubject.asObservable();

    marketData$ = this.marketDataSubject.asObservable();

    constructor() {
        this.client.onConnect = () => {
            this.client.subscribe('/topic/market-data', (message) => {
                const data: MarketDataWebSocketMessage =
                    JSON.parse(message.body);

                this.marketDataSubject.next(data);
            });
            this.client.subscribe('/topic/portfolio', (message) => {
                const data: PortfolioWebSocketMessage =
                    JSON.parse(message.body);

                this.portfolioSubject.next(data);
            });
            this.client.subscribe('/topic/portfolio', (message) => {
                const data: PortfolioWebSocketMessage =
                    JSON.parse(message.body);

                this.portfolioSubject.next(data);
            });
        };
    }

    connect() {
        if (isPlatformBrowser(this.platformId)) {
            this.client.activate();
        }
    }
}
