export interface OrderResponse{
    orderId: string;
    symbol: string;
    side: string;
    quantity: number;
    status: string;
    rejectionReason: string | null;
    price: number;
    createdAt: string;
    updatedAt: string;
}
