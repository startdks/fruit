import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Order, OrderRequest } from '../models';
import { Observable } from 'rxjs';

@Injectable({
    providedIn: 'root',
})
export class OrderService {
    private readonly API_URL = 'https://xj9iw8ynrf.execute-api.us-east-1.amazonaws.com/api/orders';

    constructor(private http: HttpClient) { }

    createOrder(request: OrderRequest): Observable<Order> {
        return this.http.post<Order>(`${this.API_URL}`, request);
    }

    getUserOrders(userId: number): Observable<Order[]> {
        const params = new HttpParams().set('userId', userId.toString());
        return this.http.get<Order[]>(`${this.API_URL}`, { params });
    }

    getOrderById(orderId: number): Observable<Order> {
        return this.http.get<Order>(`${this.API_URL}/${orderId}`);
    }

    updateOrderStatus(orderId: number, status: string): Observable<Order> {
        const params = new HttpParams().set('status', status);
        return this.http.patch<Order>(`${this.API_URL}/${orderId}/status`, null, { params });
    }

    getAllOrders(): Observable<Order[]> {
        return this.http.get<Order[]>(`${this.API_URL}`);
    }
}
