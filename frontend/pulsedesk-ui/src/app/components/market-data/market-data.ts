import { ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { CardModule } from 'primeng/card';
import { CurrencyPipe, DatePipe, DecimalPipe } from '@angular/common';
import { MarketDataResponse } from '../../models/market-data-response';
import { MarketDataService } from '../../services/market-data-service';
import { MarketDataWebSocketMessage } from '../../models/market-data-websocket-message';
import { Websocket } from '../../services/websocket-service';
import { InstrumentContext } from '../../services/instrument-context';

import { FormsModule } from '@angular/forms';
import { InputTextModule } from 'primeng/inputtext';
import { ButtonModule } from 'primeng/button';

@Component({
  selector: 'app-market-data',
  imports: [CurrencyPipe, DatePipe, DecimalPipe, CardModule, FormsModule, InputTextModule, ButtonModule],
  templateUrl: './market-data.html',
  styleUrl: './market-data.scss',
})
export class MarketData implements OnInit {

  // Pode começar como indefinida
  marketData?: MarketDataResponse;

  selectedSymbol = 'AAPL';
  searchSymbol = 'AAPL';

  private marketDataService = inject(MarketDataService);

  private websocket = inject(Websocket);

  private cdr = inject(ChangeDetectorRef);

  private instrumentContext = inject(InstrumentContext);

  ngOnInit() {
    this.loadMarketData(this.selectedSymbol);

    this.websocket.marketData$.subscribe((message) => {
      this.updateFromWebSocket(message);
      this.cdr.markForCheck();
    });
  }

  updateFromWebSocket(message: MarketDataWebSocketMessage) {
    if (message.symbol === this.selectedSymbol) {
      this.marketData = message;
    }
  }

  loadMarketData(symbol: string) {
    this.marketDataService.getMarketData(symbol).subscribe({
      next: (response) => {
        this.selectedSymbol = symbol;
        this.marketData = response;

        this.instrumentContext.broadcastInstrument(symbol);

        this.cdr.markForCheck();

      },
      error: (error) => {
        console.error('Erro ao buscar market data', error);
      }
    });
  }

  searchMarketData() {
    const symbol = this.searchSymbol.trim().toUpperCase();

    if (!symbol) {
      return;
    }

    this.loadMarketData(symbol);
  }
}
