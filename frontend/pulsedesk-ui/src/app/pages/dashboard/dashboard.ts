import { ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { Analytics } from '../../components/analytics/analytics';
import { MarketData } from '../../components/market-data/market-data';
import { OrderHistory } from '../../components/order-history/order-history';
import { OrderTicket } from '../../components/order-ticket/order-ticket';
import { Portfolio } from '../../components/portfolio/portfolio';
import { Websocket } from '../../services/websocket-service';

@Component({
  selector: 'app-dashboard',
  imports: [
    MarketData,
    Portfolio,
    OrderTicket,
    OrderHistory,
    Analytics
  ],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.scss',
})
export class Dashboard implements OnInit {

  isRealtimeConnected = false;

  private websocket = inject(Websocket);
  private cdr = inject(ChangeDetectorRef);

  // ngOnInit é inicializado apenas uma vez, ent faz sentido iniciar a conexão por ali.
  ngOnInit() {
    this.websocket.connectionStatus$.subscribe((connected) => {
      this.isRealtimeConnected = connected;
      this.cdr.markForCheck();
    });

    this.websocket.connect();
  }
}
