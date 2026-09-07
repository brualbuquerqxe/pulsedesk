import { ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { CardModule } from 'primeng/card';

import { OrderResponse } from '../../models/order-response';
import { Order } from '../../services/order';

import { CurrencyPipe, DatePipe } from '@angular/common';
import { TableModule } from 'primeng/table';
import { Websocket } from '../../services/websocket-service';

@Component({
  selector: 'app-order-history',
  imports: [CardModule, TableModule, DatePipe, CurrencyPipe],
  templateUrl: './order-history.html',
  styleUrl: './order-history.scss',
})

export class OrderHistory implements OnInit {

  private orderService = inject(Order);

  private webSocket = inject(Websocket);

  private cdr = inject(ChangeDetectorRef);

  orders: OrderResponse[] = [];

  private readonly userId =
    '33333333-3333-3333-3333-333333333333';

  ngOnInit(): void {

    this.loadOrders();

    this.webSocket.order$.subscribe((message) => {

      if (message.userId === this.userId) {
        this.loadOrders();
      }

    });
  }

  loadOrders(): void {
    this.orderService.getOrders(this.userId)
      .subscribe({
        next: (orders) => {
          this.orders = orders;
          this.cdr.markForCheck();

        },
        error: (error) => {
          console.error('Error loading orders', error);
        }
      });
  }
}
