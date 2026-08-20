import { isPlatformBrowser } from '@angular/common';
import { inject, PLATFORM_ID, Service } from '@angular/core';
import { Client } from '@stomp/stompjs';
import { MarketDataWebSocketMessage } from '../models/market-data-websocket-message';
import { PortfolioWebSocketMessage } from '../models/portfolio-websocket-message';
import { OrderWebSocketMessage } from '../models/order-websocket-message';
import { AnalyticsWebSocketMessage } from '../models/analytics-websocket-message';
import { BehaviorSubject, Subject } from 'rxjs';

@Service()
export class Websocket {

    private platformId = inject(PLATFORM_ID);

    private client = new Client({
        brokerURL: 'ws://localhost:8084/ws',
        reconnectDelay: 5000
    });

    private marketDataSubject = new Subject<MarketDataWebSocketMessage>();

    private portfolioSubject = new Subject<PortfolioWebSocketMessage>();

    private orderSubject = new Subject<OrderWebSocketMessage>();

    private analyticsSubject = new Subject<AnalyticsWebSocketMessage>();

    private connectionSubject = new BehaviorSubject<boolean>(false);

    connectionStatus$ = this.connectionSubject.asObservable();

    portfolio$ = this.portfolioSubject.asObservable();

    marketData$ = this.marketDataSubject.asObservable();

    order$ = this.orderSubject.asObservable();

    analytics$ = this.analyticsSubject.asObservable();

    constructor() {
        this.client.onConnect = () => {
            this.connectionSubject.next(true);

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

            this.client.subscribe('/topic/orders', (message) => {
                const data: OrderWebSocketMessage =
                    JSON.parse(message.body);

                this.orderSubject.next(data);
            });

            this.client.subscribe('/topic/analytics', (message) => {
                const data: AnalyticsWebSocketMessage =
                    JSON.parse(message.body);

                this.analyticsSubject.next(data);
            });
        };

        this.client.onWebSocketClose = (event) => {
            this.connectionSubject.next(false);
        };

        this.client.onWebSocketError = (error) => {
            console.error('Erro no WebSocket:', error);
            this.connectionSubject.next(false);
        };

        this.client.onStompError = (frame) => {
            console.error('Erro STOMP:', frame);
            this.connectionSubject.next(false);
        };
    }

    connect() {
        if (isPlatformBrowser(this.platformId)) {
            this.client.activate();
        }
    }
}

