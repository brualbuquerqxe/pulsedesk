import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';

export interface PortfolioHistoryPoint {
  date: string;
  totalValue: number;
}

@Injectable({
  providedIn: 'root'
})
export class PortfolioHistoryService {

  private http = inject(HttpClient);

  getPortfolioHistory(userId: string): Observable<PortfolioHistoryPoint[]> {
    return this.http.get<PortfolioHistoryPoint[]>(
      `http://localhost:8083/api/portfolio/${userId}/history`
    );
  }
}
