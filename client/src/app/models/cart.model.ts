export interface Cart {
    items: CartItem[];
    subtotal: number;
    tax: number;
    total: number;
    itemCount: number;
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

export interface CartItemRequest {
    productId: number;
    quantity: number;
    userId?: number | null;
    guestToken?: string | null;
}
