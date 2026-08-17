export interface CreateOrderRequest {
  userId: string;
  symbol: string;
  side: 'BUY' | 'SELL';
  quantity: number;
}
