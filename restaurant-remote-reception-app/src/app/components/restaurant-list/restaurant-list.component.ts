import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import {IShoppingCenter, ShoppingCenterService} from '../../services/shopping-center.service';
import {IRestaurant, RestaurantService} from '../../services/restaurant.service';
import {AlertService} from '../../services/alert.service';
import {STATUS} from '../../models/status.model';
import {CustomErrorHandlerService} from '../../services/custom-error-handler.service';

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
    private alertService: AlertService,
    private errorHandler: CustomErrorHandlerService
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
        this.errorHandler.handleError(err, '', '', 'Shopping center');
      }
    );
  }

  private getListOfRestaurants() {
    this.restaurantService.getList(this.shoppingCenterId).subscribe(
      (res: IRestaurant[]) => {
      this.restaurants = res;
      },
      err => {
        this.errorHandler.handleError(err, '', '', 'List of restaurants');
      }
    );
  }
}
