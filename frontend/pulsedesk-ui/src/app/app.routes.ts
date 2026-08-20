import { Routes } from '@angular/router';

import { AppLayout } from './layout/app-layout/app-layout';
import { Dashboard } from './pages/dashboard/dashboard';
import { Orders } from './pages/orders/orders';
import { Market } from './pages/market/market';
import { PortfolioPage } from './pages/portfolio-page/portfolio-page';
import { Settings } from './pages/settings/settings';

export const routes: Routes = [
  {
    path: '',
    component: AppLayout,
    children: [
      {
        path: '',
        component: Dashboard
      },
      {
        path: 'orders',
        component: Orders
      },
      {
        path: 'market',
        component: Market
      },
      {
        path: 'portfolio',
        component: PortfolioPage
      },
      {
        path: 'settings',
        component: Settings
      }
    ]
  }
];
