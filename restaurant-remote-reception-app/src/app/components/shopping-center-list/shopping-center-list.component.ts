import { Component, OnInit } from '@angular/core';
import {Router} from '@angular/router';
import {HttpClient} from '@angular/common/http';
import {environment} from '../../../environments/environment';
import {IShoppingCenter, ShoppingCenterService} from '../../services/shopping-center.service';
import {AppUtils} from '../../app-common';
import {AlertService} from '../../services/alert.service';

@Component({
  selector: 'app-shopping-centers',
  templateUrl: './shopping-center-list.component.html',
  styleUrls: ['./shopping-center-list.component.css']
})

export class ShoppingCenterListComponent implements OnInit {
  shoppingCenters: IShoppingCenter[];

  constructor (private shoppingCenterService: ShoppingCenterService,
               private alertService: AlertService) {}

  ngOnInit() {
    this.getListOfShoppingCenters();
  }

  private getListOfShoppingCenters() {
    this.shoppingCenterService.getList().subscribe(
      (res: IShoppingCenter[]) => {
        this.shoppingCenters = res;
      },
      err => {
        if (err.error instanceof Error) { // browser error
          this.alertService.error(0, err.error.message);
        } else if (err.error.message == null) { // non-customised error
          this.alertService.error(err.status, err.statusText);
        } else if (err.error.status_code >= 5000 ) { // server error with message not to be shown on UI
          this.alertService.error(err.error.status_code, err.statusText);
        } else if (err.error.status_code === 4011) {
          this.alertService.error(err.error.status_code, 'Please login');
        } else if (err.error.status_code === 4040) {
          this.alertService.error(err.error.status_code, 'List of shopping centers not found');
        } else {
          this.alertService.error(err.error.status_code, err.error.message);
        }
      }
    );
  }

}
