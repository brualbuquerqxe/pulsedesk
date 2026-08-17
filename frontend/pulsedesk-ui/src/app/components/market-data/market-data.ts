import { ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { CardModule } from 'primeng/card';

import { MarketDataResponse } from '../../models/market-data-response';
import { MarketDataService } from '../../services/market-data-service';
import { MarketDataWebSocketMessage } from '../../models/market-data-websocket-message';
import { Websocket } from '../../services/websocket-service';

@Component({
  selector: 'app-market-data',
  imports: [CardModule],
  templateUrl: './market-data.html',
  styleUrl: './market-data.scss',
})
export class MarketData implements OnInit {

  // Pode começar como indefinida
  marketData?: MarketDataResponse;

  private marketDataService = inject(MarketDataService);

  private websocket = inject(Websocket);

  private cdr = inject(ChangeDetectorRef);

  ngOnInit() {
    this.marketDataService.getMarketData('AAPL').subscribe({
      next: (response) => {
        this.marketData = response;
      },
      error: (error) => {
        console.error('Erro ao buscar market data', error);
      }
    });

    this.websocket.marketData$.subscribe((message) => {
      this.updateFromWebSocket(message);
    });

    this.websocket.marketData$.subscribe((message) => {

      this.updateFromWebSocket(message);

      this.cdr.markForCheck();
    });
  }

  updateFromWebSocket(message: MarketDataWebSocketMessage) {
    this.marketData = message;
  }
}
