import { HttpClient } from '@angular/common/http';
import { inject, Service } from '@angular/core';
import { PortfolioResponse } from '../models/portfolio-response';

@Service()
export class PortfolioService {
    private http = inject(HttpClient);

    getPortfolio(userId: string) {
        return this.http.get<PortfolioResponse>(
            `http://localhost:8083/api/portfolio/${userId}`
        );
    }
}
