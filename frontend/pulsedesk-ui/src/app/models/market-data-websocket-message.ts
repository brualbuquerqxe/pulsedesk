export interface MarketDataWebSocketMessage {
  symbol: string;
  price: number;
  percentageChange: number;
  timestamp: string;
}
