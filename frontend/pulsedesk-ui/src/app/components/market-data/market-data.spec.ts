import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MarketData } from './market-data';

describe('MarketData', () => {
  let component: MarketData;
  let fixture: ComponentFixture<MarketData>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MarketData],
    }).compileComponents();

    fixture = TestBed.createComponent(MarketData);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
