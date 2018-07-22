import {Component, Input, OnInit} from '@angular/core';
import {IReservedTicket} from '../../models/ticket.model';
import {IRestaurant, RestaurantService} from '../../services/restaurant.service';
import {IShoppingCenter, ShoppingCenterService} from '../../services/shopping-center.service';
import {AlertService} from '../../services/alert.service';
import {STATUS} from '../../models/status.model';

@Component({
  selector: 'app-user-ticket-item',
  templateUrl: './user-ticket-item.component.html',
  styleUrls: ['./user-ticket-item.component.css']
})
export class UserTicketItemComponent implements OnInit {
  @Input() reservedTicket: IReservedTicket;
  restaurant: IRestaurant;
  shoppingCenter: IShoppingCenter;

  constructor(private shoppingCenterService: ShoppingCenterService,
              private restaurantService: RestaurantService,
              private alertService: AlertService) { }

  ngOnInit() {
    this.getRestaurantInfo();
    this.getShoppingCenterInfo();
  }

  private getRestaurantInfo() {
    this.restaurantService.getInfo(this.reservedTicket.restaurant_id.toString()).subscribe(
      (res: IRestaurant) => {
        this.restaurant = res;
      },
      err => {
        if (err.error instanceof Error) { // browser error
          this.alertService.error(0, err.error.message);
        } else if (err.error.message == null) { // non-customised error
          this.alertService.error(err.status, err.statusText);
        } else if (err.error.status_code >= STATUS['INTERNAL_SERVER_ERROR']) { // server error with message not to be shown on UI
          this.alertService.error(err.error.status_code, err.statusText);
        } else if (err.error.status_code === STATUS['UNAUTHORIZED']) {
          this.alertService.error(err.error.status_code, 'Please login');
        } else if (err.error.status_code === STATUS['UNSUPPORTED_FORMAT']) {
          this.alertService.error(err.error.status_code, 'Restaurant not found');
        } else {
          this.alertService.error(err.error.status_code, err.error.message);
        }
      }
    );
  }

  private getShoppingCenterInfo() {
    this.shoppingCenterService.getInfo(this.restaurant.shopping_center_id.toString()).subscribe(
      (res: IShoppingCenter) => {
        this.shoppingCenter = res;
      },
      err => {
        if (err.error instanceof Error) { // browser error
          console.log('Client-side error occured: ' + err.error.message);
        } else if (err.error.message == null) { // non-customised error
          this.alertService.error(err.error.status, err.statusText);
        } else if (err.error.status_code >= STATUS['INTERNAL_SERVER_ERROR']) { // server error with message not to be shown on UI
          this.alertService.error(err.error.status_code, err.statusText);
        } else if (err.error.status_code === STATUS['UNAUTHORIZED']) {
          this.alertService.error(err.error.status_code, 'Please login');
        } else if (err.error.status_code === STATUS['RESOURCE_NOT_FOUND']) {
          this.alertService.error(err.error.status_code, 'Shopping center not found');
        } else {
          this.alertService.error(err.error.status_code, err.error.message);
        }
      }
    );
  }


}
