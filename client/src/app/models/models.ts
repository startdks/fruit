export interface User {
    id: number;
    fullName: string;
    email: string;
    createdAt?: Date;
    updatedAt?: Date;
}

export interface Cart {
    items: CartItem[];
    subtotal: number;
    tax: number;
    total: number;
    itemCount: number;
}

export interface Product {
    id: number;
    name: string;
    description: string;
    price: number;
    imageUrl: string;
    unit: string;
    stockQuantity: number;
    origin: string;
    isActive: boolean;
    createdAt?: Date;
    updatedAt?: Date;
}

export interface CartItem {
    id: number;
    productId: number;
    productName: string;
    productPrice: number;
    productImageUrl: string;
    productUnit: string;
    quantity: number;
    priceAtAddition: number;
    subtotal: number;
}

export interface OrderItem {
    id: number;
    productId: number;
    productName: string;
    productImageUrl: string;
    quantity: number;
    unitPrice: number;
    subtotal: number;
}

export interface Order {

    id: number;
    userId: number;
    status: string;
    totalAmount: number;
    shippingAddress: string;
    shippingCity: string;
    shippingState: string;
    shippingZip: string;
    items: OrderItem[];
    shippingPhone: string;
}

export interface ContactMessage {
    id?: number;
    name: string;
    email: string;
    subject: string;
    message: string;
    isRead?: boolean;
    cratedAt?: Date;
}

export interface LoginRequest {
    email: string;
    password: string;
}

export interface RegisterRequest {
    fullName: string;
    email: string;
    password: string;
}

export interface AuthResponse {
    userId: number | null;
    fullName: string | null;
    email: string | null;
    token: string | null;
    message: string;
}

export interface CartItemRequest {
    productId: number;
    quantity: number;
    userId?: number | null;
}

export interface OrderItemRequest {
    productId: number;
    quantity: number;
}

export interface OrderItemRequest {
    productId: number;
    quantity: number;
}

export interface OrderRequest {
    userId?: number | null;
    items: OrderItemRequest[];
    shippingAddress: string;
    shippingCity?: string;
    shippingState?: string;
    shippingZip?: string;
    shippingPhone?: string;
}

