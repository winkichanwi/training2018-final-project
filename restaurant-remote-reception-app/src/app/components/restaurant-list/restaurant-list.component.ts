import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import {IShoppingCenter, ShoppingCenterService} from '../../services/shopping-center.service';
import {IRestaurant, RestaurantService} from '../../services/restaurant.service';
import {AppUtils} from '../../app-common';
import {AlertService} from '../../services/alert.service';

@Component({
  selector: 'app-restaurant-list',
  templateUrl: './restaurant-list.component.html',
  styleUrls: ['./restaurant-list.component.css']
})

export class RestaurantListComponent implements OnInit {
  shoppingCenterId: string;
  shoppingCenter: IShoppingCenter;
  restaurants: IRestaurant[];

  constructor(
    private route: ActivatedRoute,
    private shoppingCenterService: ShoppingCenterService,
    private restaurantService: RestaurantService,
    private alertService: AlertService
  ) {}

  ngOnInit() {
    this.shoppingCenterId = this.route.snapshot.paramMap.get('shoppingCenterId');
    this.getShoppingCenterInfo();
    this.getListOfRestaurants();
  }

  private getShoppingCenterInfo() {
    this.shoppingCenterService.getInfo(this.shoppingCenterId).subscribe(
      (res: IShoppingCenter) => {
        this.shoppingCenter = res;
      },
      err => {
        if (err.error instanceof Error) { // browser error
          console.log('Client-side error occured: ' + err.error.message);
        } else if (err.error.message == null) { // non-customised error
          this.alertService.error(err.error.status, err.statusText);
        } else if (err.error.status_code >= 5000 ) { // server error with message not to be shown on UI
          this.alertService.error(err.error.status_code, err.statusText);
        } else if (err.error.status_code === 4011) {
            this.alertService.error(err.error.status_code, 'Please login');
        } else if (err.error.status_code === 4040) {
          this.alertService.error(err.error.status_code, 'Shopping center not found');
        } else {
          this.alertService.error(err.error.status_code, err.error.message);
        }
      }
    );
  }

  private getListOfRestaurants() {
    this.restaurantService.getList(this.shoppingCenterId).subscribe(
      (res: IRestaurant[]) => {
      this.restaurants = res;
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
          this.alertService.error(err.error.status_code, 'List of restaurants not found');
        } else {
          this.alertService.error(err.error.status_code, err.error.message);
        }
      }
    );
  }
}
