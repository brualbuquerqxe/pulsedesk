import { HttpClient } from '@angular/common/http';
import { inject, Service } from '@angular/core';
import { CreateOrderRequest } from '../models/create-order-request';
import { OrderResponse } from '../models/order-response';

@Service()
export class Order {
    private http = inject(HttpClient);

    createOrder(order: CreateOrderRequest) {
        return this.http.post<void>(
            'http://localhost:8082/api/orders',
            order
        );
    }

    // Espera receber uma lista de ordens
    getOrders(userId: string) {
        return this.http.get<OrderResponse[]>(
            `http://localhost:8082/api/orders/${userId}`
        );
    }
}
