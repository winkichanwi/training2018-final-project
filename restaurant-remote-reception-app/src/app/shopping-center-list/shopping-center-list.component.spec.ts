import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ShoppingCenterListComponent } from './shopping-center-list.component';

describe('ShoppingCenterListComponent', () => {
  let component: ShoppingCenterListComponent;
  let fixture: ComponentFixture<ShoppingCenterListComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ShoppingCenterListComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ShoppingCenterListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
