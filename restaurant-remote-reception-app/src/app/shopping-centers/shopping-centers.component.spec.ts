import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ShoppingCentersComponent } from './shopping-centers.component';

describe('ShoppingCentersComponent', () => {
  let component: ShoppingCentersComponent;
  let fixture: ComponentFixture<ShoppingCentersComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ShoppingCentersComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ShoppingCentersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
