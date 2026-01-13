import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Product } from '../models/models';

@Injectable({
    providedIn: 'root',
})
export class ProductService {
    private readonly API_URL = "https://xj9iw8ynrf.execute-api.us-east-1.amazonaws.com/api/products";
    constructor(private http: HttpClient) { }

    getAllProducts(isActive: boolean = true): Observable<Product[]> {
        let params = new HttpParams();
        if (isActive) {
            params = params.set("active", "true");
        }
        return this.http.get<Product[]>(this.API_URL, { params })
    }

    getProductById(id: number): Observable<Product> {
        return this.http.get<Product>(`${this.API_URL}/${id}`);
    }

    createProduct(product: Product): Observable<Product> {
        return this.http.post<Product>(this.API_URL, product);
    }

    updateProduct(id: number, product: Product): Observable<Product> {
        return this.http.put<Product>(`${this.API_URL}/${id}`, product);
    }

    deleteProduct(id: number): Observable<void> {
        return this.http.delete<void>(`${this.API_URL}/${id}`);
    }

    searchProducts(name: string): Observable<Product[]> {
        const params = new HttpParams().set("name", name);
        return this.http.get<Product[]>(`${this.API_URL}/search`, { params });
    }
}
