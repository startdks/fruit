import { provideZonelessChangeDetection } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { ProductService } from './product.service';
import { Product } from '../models/models';

describe('ProductService', () => {
    let service: ProductService;
    let httpMock: HttpTestingController;

    const API_URL = 'http://localhost:8080/api/products';

    // Mock product data
    const mockProduct: Product = {
        id: 1,
        name: 'Apple',
        description: 'Fresh red apple',
        price: 2.99,
        imageUrl: '/images/apple.jpg',
        unit: 'kg',
        stockQuantity: 100,
        origin: 'Korea',
        isActive: true
    };

    const mockProducts: Product[] = [
        mockProduct,
        {
            id: 2,
            name: 'Banana',
            description: 'Yellow banana',
            price: 1.99,
            imageUrl: '/images/banana.jpg',
            unit: 'kg',
            stockQuantity: 50,
            origin: 'Philippines',
            isActive: true
        }
    ];

    beforeEach(() => {
        TestBed.configureTestingModule({
            providers: [
                provideZonelessChangeDetection(),
                provideHttpClient(),
                provideHttpClientTesting(),
                ProductService
            ]
        });

        service = TestBed.inject(ProductService);
        httpMock = TestBed.inject(HttpTestingController);
    });

    afterEach(() => {
        // Verify no outstanding HTTP requests
        httpMock.verify();
    });

    it('should be created', () => {
        expect(service).toBeTruthy();
    });

    describe('getAllProducts', () => {
        it('should fetch all active products by default', () => {
            service.getAllProducts().subscribe(products => {
                expect(products.length).toBe(2);
                expect(products).toEqual(mockProducts);
            });

            const req = httpMock.expectOne(`${API_URL}?active=true`);
            expect(req.request.method).toBe('GET');
            req.flush(mockProducts);
        });

        it('should fetch all products when isActive is false', () => {
            service.getAllProducts(false).subscribe(products => {
                expect(products).toEqual(mockProducts);
            });

            const req = httpMock.expectOne(API_URL);
            expect(req.request.method).toBe('GET');
            req.flush(mockProducts);
        });
    });

    describe('getProductById', () => {
        it('should fetch a single product by ID', () => {
            service.getProductById(1).subscribe(product => {
                expect(product).toEqual(mockProduct);
                expect(product.id).toBe(1);
                expect(product.name).toBe('Apple');
            });

            const req = httpMock.expectOne(`${API_URL}/1`);
            expect(req.request.method).toBe('GET');
            req.flush(mockProduct);
        });
    });

    describe('createProduct', () => {
        it('should create a new product', () => {
            const newProduct: Product = {
                id: 0,
                name: 'Orange',
                description: 'Fresh orange',
                price: 3.99,
                imageUrl: '/images/orange.jpg',
                unit: 'kg',
                stockQuantity: 75,
                origin: 'USA',
                isActive: true
            };

            const createdProduct = { ...newProduct, id: 3 };

            service.createProduct(newProduct).subscribe(product => {
                expect(product).toEqual(createdProduct);
                expect(product.id).toBe(3);
            });

            const req = httpMock.expectOne(API_URL);
            expect(req.request.method).toBe('POST');
            expect(req.request.body).toEqual(newProduct);
            req.flush(createdProduct);
        });
    });

    describe('updateProduct', () => {
        it('should update an existing product', () => {
            const updatedProduct: Product = {
                ...mockProduct,
                price: 3.49,
                stockQuantity: 80
            };

            service.updateProduct(1, updatedProduct).subscribe(product => {
                expect(product.price).toBe(3.49);
                expect(product.stockQuantity).toBe(80);
            });

            const req = httpMock.expectOne(`${API_URL}/1`);
            expect(req.request.method).toBe('PUT');
            expect(req.request.body).toEqual(updatedProduct);
            req.flush(updatedProduct);
        });
    });

    describe('deleteProduct', () => {
        it('should delete a product', () => {
            service.deleteProduct(1).subscribe(response => {
                expect(response).toBeNull();
            });

            const req = httpMock.expectOne(`${API_URL}/1`);
            expect(req.request.method).toBe('DELETE');
            req.flush(null);
        });
    });

    describe('searchProducts', () => {
        it('should search products by name', () => {
            const searchResults = [mockProduct];

            service.searchProducts('Apple').subscribe(products => {
                expect(products.length).toBe(1);
                expect(products[0].name).toBe('Apple');
            });

            const req = httpMock.expectOne(`${API_URL}/search?name=Apple`);
            expect(req.request.method).toBe('GET');
            req.flush(searchResults);
        });

        it('should return empty array when no products match', () => {
            service.searchProducts('NonExistent').subscribe(products => {
                expect(products.length).toBe(0);
            });

            const req = httpMock.expectOne(`${API_URL}/search?name=NonExistent`);
            expect(req.request.method).toBe('GET');
            req.flush([]);
        });
    });

    describe('Error Handling', () => {
        it('should handle HTTP errors', () => {
            service.getProductById(999).subscribe({
                next: () => fail('should have failed'),
                error: (error) => {
                    expect(error.status).toBe(404);
                }
            });

            const req = httpMock.expectOne(`${API_URL}/999`);
            req.flush('Product not found', { status: 404, statusText: 'Not Found' });
        });
    });
});
