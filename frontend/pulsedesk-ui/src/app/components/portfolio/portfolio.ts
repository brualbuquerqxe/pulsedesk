import { ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { CardModule } from 'primeng/card';
import { TableModule } from 'primeng/table';
import { CurrencyPipe } from '@angular/common';

import { PortfolioResponse } from '../../models/portfolio-response';
import { PortfolioService } from '../../services/portfolio-service';
import { PortfolioWebSocketMessage } from '../../models/portfolio-websocket-message';
import { Websocket } from '../../services/websocket-service';

@Component({
  selector: 'app-portfolio',
  imports: [CurrencyPipe, CardModule, TableModule],
  templateUrl: './portfolio.html',
  styleUrl: './portfolio.scss',
})
export class Portfolio implements OnInit {

  portfolio?: PortfolioResponse;

  private portfolioService = inject(PortfolioService);
  private websocket = inject(Websocket);
  private cdr = inject(ChangeDetectorRef);

  ngOnInit() {
    this.portfolioService
      .getPortfolio('11111111-1111-1111-1111-111111111111')
      .subscribe({
        next: (response) => {
          this.portfolio = response;

          this.cdr.markForCheck();
        },
        error: (error) => {
          console.error('Erro ao buscar portfolio', error);
        }
      });

    this.websocket.portfolio$.subscribe((message) => {
      this.updateFromWebSocket(message);
      this.cdr.markForCheck();
    });
  }

  updateFromWebSocket(message: PortfolioWebSocketMessage) {

    if (!this.portfolio) {
      return;
    }

    this.portfolio.cashBalance = message.cashBalance;

    const index = this.portfolio.positions.findIndex(
      position => position.symbol === message.symbol
    );

    if (message.quantity === 0) {
      this.portfolio.positions = this.portfolio.positions.filter(
        position => position.symbol !== message.symbol
      );
      return;
    }

    if (index >= 0) {
      this.portfolio.positions[index] = {
        symbol: message.symbol,
        quantity: message.quantity,
        averagePrice: message.averagePrice,
        lastPrice: message.lastPrice
      };

      this.portfolio.positions = [...this.portfolio.positions];
    } else {
      this.portfolio.positions = [
        ...this.portfolio.positions,
        {
          symbol: message.symbol,
          quantity: message.quantity,
          averagePrice: message.averagePrice,
          lastPrice: message.lastPrice
        }
      ];
    }
  }

  get positionsValue(): number {
    if (!this.portfolio) {
      return 0;
    }

    return this.portfolio.positions.reduce(
      (total, position) =>
        total + position.quantity * position.lastPrice,
      0
    );
  }

  get positionsCost(): number {
    if (!this.portfolio) {
      return 0;
    }

    return this.portfolio.positions.reduce(
      (total, position) =>
        total + position.quantity * position.averagePrice,
      0
    );
  }

  get unrealizedPnL(): number {
    return this.positionsValue - this.positionsCost;
  }

  get totalEquity(): number {
    if (!this.portfolio) {
      return 0;
    }

    return this.portfolio.cashBalance + this.positionsValue;
  }
}
