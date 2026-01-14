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
