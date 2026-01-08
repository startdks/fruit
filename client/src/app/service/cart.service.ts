import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { AuthService } from './auth.service';
import { Cart, CartItem, CartItemRequest } from '../models/models';

@Injectable({
    providedIn: 'root',
})
export class CartService {
    private readonly API_URL = 'http://localhost:8080/api/cart';
    private cartCountSubject = new BehaviorSubject<number>(0);
    public cartCount$ = this.cartCountSubject.asObservable();

    constructor(
        private http: HttpClient,
        private authService: AuthService
    ) { }

    getCart(userId?: number | null): Observable<Cart> {
        let params = new HttpParams();
        if (userId) {
            params = params.set('userId', userId.toString());
        }
        return this.http.get<Cart>(this.API_URL, { params }).pipe(
            tap(cart => this.cartCountSubject.next(cart.itemCount))
        );
    }

    addToCart(productId: number, quantity: number = 1): Observable<CartItem> {
        const userId = this.authService.getCurrentUser()?.userId;
        const request: CartItemRequest = {
            productId,
            quantity,
            userId: userId || null
        };
        return this.http.post<CartItem>(this.API_URL, request);
    }

    updateQuantity(cartItemId: number, quantity: number): Observable<CartItem> {
        const params = new HttpParams().set('quantity', quantity.toString());
        return this.http.put<CartItem>(`${this.API_URL}/${cartItemId}`, null, { params })
    }

    removeFromCart(cartItemId: number): Observable<void> {
        return this.http.delete<void>(`${this.API_URL}/${cartItemId}`);
    }

    clearCart(userId: number): Observable<void> {
        const params = new HttpParams().set('userId', userId.toString());
        return this.http.delete<void>(`${this.API_URL}/clear`, { params }).pipe(
            tap(() => this.cartCountSubject.next(0)));
    }

    transferGuestCart(userId: number): Observable<void> {
        const params = new HttpParams().set('userId', userId.toString());
        return this.http.post<void>(`${this.API_URL}/transfer`, null, { params });
    }

    getCartCount(): number {
        return this.cartCountSubject.value;
    }
}
