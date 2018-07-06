import { Component, OnInit } from '@angular/core';
import {Router} from '@angular/router';
import {HttpClient} from '@angular/common/http';
import {environment} from '../../environments/environment';
import {IShoppingCenter, ShoppingCenterService} from '../services/shopping-center.service';

@Component({
  selector: 'app-shopping-centers',
  templateUrl: './shopping-center-list.component.html',
  styleUrls: ['./shopping-center-list.component.css']
})

export class ShoppingCenterListComponent implements OnInit {
  shoppingCenters: IShoppingCenter[];

  constructor (private shoppingCenterService: ShoppingCenterService) {
    this.shoppingCenterService.getList().subscribe((res: IShoppingCenter[]) => {
      this.shoppingCenters = res;
    });
  }

  ngOnInit() {
  }

}
