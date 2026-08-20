import { CardModule } from 'primeng/card';
import { ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';

import { Order } from '../../services/order';
import { OrderResponse } from '../../models/order-response';

import { TableModule } from 'primeng/table';
import {CurrencyPipe, DatePipe, DecimalPipe } from '@angular/common';
import { Websocket } from '../../services/websocket-service';

@Component({
  selector: 'app-order-history',
  imports: [CardModule, TableModule, DatePipe, DecimalPipe, CurrencyPipe],
  templateUrl: './order-history.html',
  styleUrl: './order-history.scss',
})

export class OrderHistory implements OnInit {

  private orderService = inject(Order);

  private webSocket = inject(Websocket);

  private cdr = inject(ChangeDetectorRef);

  orders: OrderResponse[] = [];

  private readonly userId =
    '11111111-1111-1111-1111-111111111111';

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
