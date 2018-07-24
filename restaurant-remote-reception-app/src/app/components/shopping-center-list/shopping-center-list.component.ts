import { Component, OnInit } from '@angular/core';
import {IShoppingCenter, ShoppingCenterService} from '../../services/shopping-center.service';
import {AlertService} from '../../services/alert.service';
import {STATUS} from '../../models/status.model';
import {Router} from '@angular/router';

@Component({
  selector: 'app-shopping-centers',
  templateUrl: './shopping-center-list.component.html',
  styleUrls: ['./shopping-center-list.component.css']
})

export class ShoppingCenterListComponent implements OnInit {
  shoppingCenters: IShoppingCenter[];

  constructor (private shoppingCenterService: ShoppingCenterService,
               private alertService: AlertService,
               private router: Router) {}

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
        } else if (err.error.status_code >= STATUS['INTERNAL_SERVER_ERROR']) { // server error with message not to be shown on UI
          this.alertService.error(err.error.status_code, err.statusText);
        } else if (err.error.status_code === STATUS['UNAUTHORIZED']) {
          localStorage.removeItem('authenticated');
          this.alertService.error(err.error.status_code, 'Please login before continue browsing.', true);
          this.router.navigate(['/login'], { queryParams: {returnUrl: this.router.url} });
        } else if (err.error.status_code === STATUS['RESOURCE_NOT_FOUND']) {
          this.alertService.error(err.error.status_code, 'List of shopping centers not found');
        } else {
          this.alertService.error(err.error.status_code, err.error.message);
        }
      }
    );
  }

}
