import { Component, OnInit } from '@angular/core';
import {IShoppingCenter, ShoppingCenterService} from '../services/shopping-center.service';
import {AppUtils} from '../app-common';

@Component({
  selector: 'app-shopping-centers',
  templateUrl: './shopping-center-list.component.html',
  styleUrls: ['./shopping-center-list.component.css']
})

export class ShoppingCenterListComponent implements OnInit {
  shoppingCenters: IShoppingCenter[];

  constructor (private shoppingCenterService: ShoppingCenterService) {}

  ngOnInit() {
    this.getListOfShoppingCenters();
  }

  private getListOfShoppingCenters() {
    this.shoppingCenterService.getList().subscribe(
      (res: IShoppingCenter[]) => {
        this.shoppingCenters = res;
      },
      err => {
        AppUtils.handleError(err);
      }
    );
  }

}
