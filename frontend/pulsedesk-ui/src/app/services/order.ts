import { HttpClient } from '@angular/common/http';
import { inject, Service } from '@angular/core';
import { CreateOrderRequest } from '../models/create-order-request';

@Service()
export class Order {
    private http = inject(HttpClient);

    createOrder(order: CreateOrderRequest) {
        return this.http.post<void>(
            'http://localhost:8082/api/orders',
            order
        );
    }
}
