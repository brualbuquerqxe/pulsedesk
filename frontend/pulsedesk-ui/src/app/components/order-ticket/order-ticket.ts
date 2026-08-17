import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CardModule } from 'primeng/card';
import { InputTextModule } from 'primeng/inputtext';
import { InputNumberModule } from 'primeng/inputnumber';
import { ButtonModule } from 'primeng/button';
import { SelectButtonModule } from 'primeng/selectbutton';
import { Order } from '../../services/order';
import { CreateOrderRequest } from '../../models/create-order-request';

@Component({
  selector: 'app-order-ticket',
  imports: [FormsModule, CardModule, InputTextModule, InputNumberModule, ButtonModule, SelectButtonModule],
  templateUrl: './order-ticket.html',
  styleUrl: './order-ticket.scss',
})
export class OrderTicket {
  symbol = '';
  side: 'BUY' | 'SELL' = 'BUY';
  quantity = 1;
  sideOptions = ['BUY', 'SELL'];

  private orderService = inject(Order);

  submitOrder() {

    const order: CreateOrderRequest = {
      userId: "11111111-1111-1111-1111-111111111111",
      symbol: this.symbol,
      side: this.side,
      quantity: this.quantity
    };

    // Subscribe: a chamada HHTP é finalmente disparada
    this.orderService.createOrder(order).subscribe({
      next: () => {      },
      error: (error) => {
        console.error('Erro ao enviar ordem', error);
      }
    });
  }

}
