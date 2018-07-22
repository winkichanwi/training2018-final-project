import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { UserTicketItemComponent } from './user-ticket-item.component';

describe('UserTicketItemComponent', () => {
  let component: UserTicketItemComponent;
  let fixture: ComponentFixture<UserTicketItemComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ UserTicketItemComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(UserTicketItemComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
