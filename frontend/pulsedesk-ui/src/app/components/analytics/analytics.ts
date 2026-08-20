import { ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { DecimalPipe } from '@angular/common';

import { CardModule } from 'primeng/card';
import { AnalyticsWebSocketMessage } from '../../models/analytics-websocket-message';
import { Websocket } from '../../services/websocket-service';


@Component({
  selector: 'app-analytics',
  imports: [DecimalPipe, CardModule],
  templateUrl: './analytics.html',
  styleUrl: './analytics.scss',
})
export class Analytics implements OnInit {

  analytics?: AnalyticsWebSocketMessage;

  private websocket = inject(Websocket);

  private cdr = inject(ChangeDetectorRef);

  ngOnInit() {
    this.websocket.analytics$.subscribe((message) => {
      this.analytics = message;
      this.cdr.markForCheck();
    });
  }
}
