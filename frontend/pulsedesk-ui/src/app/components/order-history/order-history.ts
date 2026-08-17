import { Component } from '@angular/core';
import { CardModule } from 'primeng/card';

@Component({
  selector: 'app-order-history',
  imports: [CardModule],
  templateUrl: './order-history.html',
  styleUrl: './order-history.scss',
})
export class OrderHistory {}
