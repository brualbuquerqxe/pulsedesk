export interface PortfolioWebSocketMessage {
  portfolioId: string;
  userId: string;
  symbol: string;
  quantity: number;
  averagePrice: number;
  lastPrice: number;
  cashBalance: number;
  timestamp: string;
}
