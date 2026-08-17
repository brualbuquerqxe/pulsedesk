import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OrderTicket } from './order-ticket';

describe('OrderTicket', () => {
  let component: OrderTicket;
  let fixture: ComponentFixture<OrderTicket>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [OrderTicket],
    }).compileComponents();

    fixture = TestBed.createComponent(OrderTicket);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
