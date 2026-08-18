export interface OrderWebSocketMessage {
  orderId: string;
  userId: string;
  symbol: string;
  side: string;
  quantity: number;
  status: string;
  price: number | null;
  rejectionReason: string | null;
  timestamp: string;
}
