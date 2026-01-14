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
