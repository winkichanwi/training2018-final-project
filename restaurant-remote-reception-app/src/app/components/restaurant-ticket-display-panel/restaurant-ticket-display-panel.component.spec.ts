import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { RestaurantTicketDisplayPanelComponent } from './restaurant-ticket-display-panel.component';

describe('RestaurantTicketDisplayPanelComponent', () => {
  let component: RestaurantTicketDisplayPanelComponent;
  let fixture: ComponentFixture<RestaurantTicketDisplayPanelComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ RestaurantTicketDisplayPanelComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RestaurantTicketDisplayPanelComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
