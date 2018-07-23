import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { RestaurantTicketItemComponent } from './restaurant-ticket-item.component';

describe('RestaurantTicketItemComponent', () => {
  let component: RestaurantTicketItemComponent;
  let fixture: ComponentFixture<RestaurantTicketItemComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ RestaurantTicketItemComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RestaurantTicketItemComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
