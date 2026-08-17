import { TestBed } from '@angular/core/testing';

import { MarketData } from './market-data';

describe('MarketData', () => {
  let service: MarketData;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(MarketData);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
