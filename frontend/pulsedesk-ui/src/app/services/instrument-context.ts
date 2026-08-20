import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';

export interface Fdc3InstrumentContext {
  type: 'fdc3.instrument';
  id: {
    ticker: string;
  };
}

@Injectable({
  providedIn: 'root'
})
export class InstrumentContext {

  private instrumentSubject =
    new Subject<Fdc3InstrumentContext>();

  instrument$ =
    this.instrumentSubject.asObservable();

  broadcastInstrument(symbol: string) {
    const context: Fdc3InstrumentContext = {
      type: 'fdc3.instrument',
      id: {
        ticker: symbol
      }
    };

    this.instrumentSubject.next(context);
  }
}
