export interface OrderItem {
    id: number;
    quantity: number;
}

export interface ConfirmedOrderRequest {
    address: string;
    items: OrderItem[];
}

export interface OrderResponse {
    id: number;
    image: string;
    productName: string;
    price: number;
    quantity: number;
    discount: number;
    address: string;
}
