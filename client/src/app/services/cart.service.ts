import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, of, switchMap, tap } from 'rxjs';
import { AuthService } from './auth.service';
import { Cart, CartItem, CartItemRequest } from '../models';

@Injectable({
    providedIn: 'root',
})
export class CartService {
    private readonly API_URL = 'https://xj9iw8ynrf.execute-api.us-east-1.amazonaws.com/api/cart';
    private cartCountSubject = new BehaviorSubject<number>(0);
    public cartCount$ = this.cartCountSubject.asObservable();

    constructor(
        private http: HttpClient,
        private authService: AuthService
    ) { }

    private getGuestToken(): Observable<string> {
        const token = localStorage.getItem('guestToken');
        if (token) {
            return of(token);
        }
        return this.http.get(`${this.API_URL}/guest-token`, { responseType: 'text' }).pipe(
            tap(guestToken => localStorage.setItem('guestToken', guestToken))
        );
    }

    private getGuestTokenSync(): string | null {
        return localStorage.getItem('guestToken');
    }

    private clearGuestToken(): void {
        localStorage.removeItem('guestToken');
    }

    getCart(userId?: number | null): Observable<Cart> {
        if (userId) {
            const params = new HttpParams().set('userId', userId.toString());
            return this.http.get<Cart>(this.API_URL, { params }).pipe(
                tap(cart => this.cartCountSubject.next(cart.itemCount))
            );
        }
        return this.getGuestToken().pipe(
            switchMap(token => {
                const params = new HttpParams().set('guestToken', token);
                return this.http.get<Cart>(this.API_URL, { params });
            }),
            tap(cart => this.cartCountSubject.next(cart.itemCount))
        );
    }


    addToCart(productId: number, quantity: number = 1): Observable<CartItem> {
        const userId = this.authService.getCurrentUser()?.userId;
        if (userId) {
            const request: CartItemRequest = {
                productId,
                quantity,
                userId: userId || null,
                guestToken: null
            };
            return this.http.post<CartItem>(this.API_URL, request);
        }
        return this.getGuestToken().pipe(
            switchMap(token => {
                const request: CartItemRequest = {
                    productId,
                    quantity,
                    userId: null,
                    guestToken: token
                };
                return this.http.post<CartItem>(this.API_URL, request);
            })
        );
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
        const guestToken = this.getGuestTokenSync();
        if (!guestToken) {
            return of(undefined);
        }
        const params = new HttpParams()
            .set('userId', userId.toString())
            .set('guestToken', guestToken);
        return this.http.post<void>(`${this.API_URL}/transfer`, null, { params })
            .pipe(tap(() => this.clearGuestToken()
            ));
    }

    getCartCount(): number {
        return this.cartCountSubject.value;
    }
}
