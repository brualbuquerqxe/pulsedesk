import { ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { ChartModule } from 'primeng/chart';
import { ChartData, ChartOptions } from 'chart.js';
import { CardModule } from 'primeng/card';

import {
    PortfolioHistoryService
} from '../../services/portfolio-history-service';

@Component({
    selector: 'app-portfolio-history',
    imports: [
        ChartModule,
        CardModule
    ],
    templateUrl: './portfolio-history.html',
    styleUrl: './portfolio-history.scss',
})
export class PortfolioHistory implements OnInit {

    private cdr = inject(ChangeDetectorRef);

    private portfolioHistoryService =
        inject(PortfolioHistoryService);

    private readonly userId =
        '11111111-1111-1111-1111-111111111111';

    chartData: ChartData<'line'> = {
        labels: [],
        datasets: []
    };

    chartOptions: ChartOptions<'line'> = {
        responsive: true,
        maintainAspectRatio: false,

        plugins: {
            legend: {
                display: false
            },

            tooltip: {
                callbacks: {
                    label: (context) => {
                        const value = context.parsed.y;

                        if (value === null) {
                            return '';
                        }

                        return `Portfolio: $${value.toLocaleString('en-US', {
                            minimumFractionDigits: 2,
                            maximumFractionDigits: 2
                        })}`;
                    }
                }
            }
        },

        scales: {
            y: {
                ticks: {
                    callback: (value) => {
                        return `$${Number(value).toLocaleString('en-US')}`;
                    }
                }
            }
        }
    };

    ngOnInit() {

        this.portfolioHistoryService
            .getPortfolioHistory(this.userId)
            .subscribe(history => {

                this.chartData = {
                    labels: history.map(point => {
                        const date = new Date(`${point.date}T00:00:00`);

                        return date.toLocaleDateString('en-US', {
                            month: 'short',
                            day: 'numeric'
                        });
                    }),

                    datasets: [
                        {
                            label: 'Portfolio Value',
                            data: history.map(point => point.totalValue),
                            tension: 0.3
                        }
                    ]
                };

                this.cdr.detectChanges();
            });
    }
}
