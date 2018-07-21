import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TicketDisplayPanelComponent } from './ticket-display-panel.component';

describe('TicketDisplayPanelComponent', () => {
  let component: TicketDisplayPanelComponent;
  let fixture: ComponentFixture<TicketDisplayPanelComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ TicketDisplayPanelComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TicketDisplayPanelComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
