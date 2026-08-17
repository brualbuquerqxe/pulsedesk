import { providePrimeNG } from 'primeng/config';
import { ApplicationConfig, provideBrowserGlobalErrorListeners } from '@angular/core';
import { provideRouter } from '@angular/router';
import Aura from '@primeuix/themes/aura';
import { provideHttpClient } from '@angular/common/http';

import { routes } from './app.routes';
import { provideClientHydration } from '@angular/platform-browser';

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideHttpClient(),
    provideRouter(routes), provideClientHydration(),
    providePrimeNG({
      license: "eyJpZCI6ImQzMDQzNjM3LWU4YWMtNGQyZC05MjE5LTUzYjhhZDRmODdlYiIsInByb2R1Y3QiOiJwcmltZXVpIiwidGllciI6ImNvbW11bml0eSIsInR5cGUiOiJkZXYiLCJpYXQiOjE3ODY3MzI5NDYsImV4cCI6MTgxODI2ODk0Nn0.kaATZKFrswesNqG57o3f-iCIfNux06PNEGwDvOb2fBMTNnlpYAJhFNZdbuFia-1awXflw_y9evmIxwZ-AGYZBQ",
      theme: {
        preset: Aura
      }
    })
  ]
};
