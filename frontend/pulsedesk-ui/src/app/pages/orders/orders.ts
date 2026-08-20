import { Component } from '@angular/core';
import { OrderHistory } from '../../components/order-history/order-history';

@Component({
  selector: 'app-orders',
  imports: [
    OrderHistory
  ],
  templateUrl: './orders.html',
  styleUrl: './orders.scss'
})
export class Orders {

}
