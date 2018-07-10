import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TickerReservationComponent } from './ticker-reservation.component';

describe('TickerReservationComponent', () => {
  let component: TickerReservationComponent;
  let fixture: ComponentFixture<TickerReservationComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ TickerReservationComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TickerReservationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
