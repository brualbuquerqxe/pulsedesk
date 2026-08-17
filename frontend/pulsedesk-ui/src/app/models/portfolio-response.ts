export interface PositionResponse {
  symbol: string;
  quantity: number;
  averagePrice: number;
  lastPrice: number;
}

export interface PortfolioResponse {
  portfolioId: string;
  cashBalance: number;
  positions: PositionResponse[];
}
