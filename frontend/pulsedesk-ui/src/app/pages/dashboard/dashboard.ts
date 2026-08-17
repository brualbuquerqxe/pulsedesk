import { Component, inject, OnInit } from '@angular/core';
import { MarketData } from '../../components/market-data/market-data';
import { Portfolio } from '../../components/portfolio/portfolio';
import { OrderTicket } from '../../components/order-ticket/order-ticket';
import { OrderHistory } from '../../components/order-history/order-history';
import { Websocket } from '../../services/websocket-service';

@Component({
  selector: 'app-dashboard',
  imports: [
    MarketData,
    Portfolio,
    OrderTicket,
    OrderHistory
  ],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.scss',
})
export class Dashboard implements OnInit {
  private websocket = inject(Websocket);

  // ngOnInit é inicializado apenas uma vez, ent faz sentido iniciar a conexão por ali.
  ngOnInit() {
    this.websocket.connect();
  }
}
