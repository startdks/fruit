import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { AuthResponse, LoginRequest, RegisterRequest } from '../models';
import { HttpClient } from '@angular/common/http';

@Injectable({
    providedIn: 'root',
})
export class AuthService {
    private readonly API_URL = "https://xj9iw8ynrf.execute-api.us-east-1.amazonaws.com/api/auth";
    private currentUserSubject = new BehaviorSubject<AuthResponse | null>(null);
    public currentUser$ = this.currentUserSubject.asObservable();

    constructor(private http: HttpClient) {
        const savedUser = localStorage.getItem('currentUser');
        if (savedUser) {
            this.currentUserSubject.next(JSON.parse(savedUser));
        }
    }

    register(request: RegisterRequest): Observable<AuthResponse> {
        return this.http.post<AuthResponse>(`${this.API_URL}/register`, request).pipe(
            tap(response => {
                if (response.userId && response.token) {
                    this.setCurrentUser(response);
                }
            })
        )
    }

    login(request: LoginRequest): Observable<AuthResponse> {
        return this.http.post<AuthResponse>(`${this.API_URL}/login`, request).pipe(
            tap(response => {
                if (response.userId && response.token) {
                    this.setCurrentUser(response);
                }
            })
        )
    }

    loout(): void {
        localStorage.removeItem('currentUser');
        localStorage.removeItem('authToken')
        this.currentUserSubject.next(null)
    }


    private setCurrentUser(user: AuthResponse): void {
        localStorage.setItem('currentUser', JSON.stringify(user));
        if (user.token) {
            localStorage.setItem('authToken', user.token);
        }
        this.currentUserSubject.next(user);
    }

    getCurrentUser(): AuthResponse | null {
        return this.currentUserSubject.value;
    }

    getToken(): string | null {
        return localStorage.getItem('authToken');
    }

    isLoggedIn(): boolean {
        return this.currentUserSubject.value !== null &&
            this.currentUserSubject.value.userId !== null &&
            this.getToken() !== null;
    }
}
