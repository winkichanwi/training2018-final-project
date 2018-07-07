import { TestBed, inject } from '@angular/core/testing';

import { ShoppingCenterService } from './shopping-center.service';

describe('ShoppingCenterService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [ShoppingCenterService]
    });
  });

  it('should be created', inject([ShoppingCenterService], (service: ShoppingCenterService) => {
    expect(service).toBeTruthy();
  }));
});
