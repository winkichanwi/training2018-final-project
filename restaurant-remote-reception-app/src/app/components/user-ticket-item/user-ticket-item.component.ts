import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {IReservedTicket, IRestaurantLastCalled, TICKET_STATUS_ACCEPTED, TICKET_STATUS_CANCELLED} from '../../models/ticket.model';
import {IRestaurant, RestaurantService} from '../../services/restaurant.service';
import {IShoppingCenter, ShoppingCenterService} from '../../services/shopping-center.service';
import {AlertService} from '../../services/alert.service';
import {STATUS} from '../../models/status.model';
import {TicketService} from '../../services/ticket.service';
import {interval} from 'rxjs';
import {Router} from '@angular/router';
import {
  trigger,
  state,
  style,
} from '@angular/animations';

const intervalCounter = interval(10000);
const animationIntervalCounter = interval(500);

@Component({
  selector: 'app-user-ticket-item',
  templateUrl: './user-ticket-item.component.html',
  animations: [
    trigger('flashState', [
      state('inactive',
        style({backgroundColor: '#E3F5FB'})
      ),
      state('active',
        style({backgroundColor: '#FFDAD3'})
      ),
    ])
  ]
})
export class UserTicketItemComponent implements OnInit, OnDestroy {
  @Input() reservedTicket: IReservedTicket;
  restaurant: IRestaurant = {id: 0, floor: '', name: '', opening_hour: '', cuisine: '', seat_no: 0,
    phone_no: '', status: '', shopping_center_id: 0, image_url: ''};
  shoppingCenter: IShoppingCenter = {id: 0, name: '', branch: ''};
  restaurantLastCalled: IRestaurantLastCalled = {ticket_type: '', last_called: 0};
  alive: boolean;
  flashState = 'inactive';

  constructor(private shoppingCenterService: ShoppingCenterService,
              private restaurantService: RestaurantService,
              private ticketService: TicketService,
              private alertService: AlertService,
              private router: Router) {
    this.alive = true;
  }

  ngOnInit() {
    this.getRestaurantInfo();
    intervalCounter.subscribe(() => {
      if (this.alive) {
        this.getRestaurantLastCalled(this.restaurantLastCalled.last_called);
      }
    });
  }

  private getRestaurantInfo() {
    this.restaurantService.getInfo(this.reservedTicket.restaurant_id.toString()).subscribe(
      (res: IRestaurant) => {
        this.restaurant = res;
        this.getShoppingCenterInfo();
        this.getRestaurantLastCalled();
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
          this.alertService.error(0, err.error.message);
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

  private getRestaurantLastCalled(originalLastCalled = -1) {
    this.ticketService.getRestaurantLastCalled(this.restaurant.id, this.reservedTicket.ticket_type).subscribe(
      (res: IRestaurantLastCalled) => {
        this.restaurantLastCalled = res;
        if (originalLastCalled !== -1 && res.last_called !== originalLastCalled) {
          this.toggleBlinking();
        }
      },
      err => {
        if (err.error instanceof Error) { // browser error
          this.alertService.error(0, err.error.message);
        } else if (err.error.message == null) { // non-customised error
          this.alertService.error(err.error.status, err.statusText);
        } else if (err.error.status_code >= STATUS['INTERNAL_SERVER_ERROR']) { // server error with message not to be shown on UI
          this.alertService.error(err.error.status_code, err.statusText);
        } else if (err.error.status_code === STATUS['UNAUTHORIZED']) {
          this.alertService.error(err.error.status_code, 'Please login');
        } else if (err.error.status_code === STATUS['RESOURCE_NOT_FOUND']) {
          this.restaurantLastCalled = { ticket_type: this.reservedTicket.ticket_type, last_called: 0 };
        } else {
          this.alertService.error(err.error.status_code, err.error.message);
        }
      }
    );
  }

  toggleBlinking() {
    let blinkCount = 0;
    this.flashState = this.flashState === 'active' ? 'inactive' : 'active';
    animationIntervalCounter.subscribe( () => {
        if (blinkCount < 5) {
          this.flashState = this.flashState === 'active' ? 'inactive' : 'active';
          blinkCount = blinkCount + 1;
        }
      }
    );
  }

  accept() {
    const ticketAcceptForm = JSON.stringify({ticket_id: this.reservedTicket.ticket_id, ticket_status: TICKET_STATUS_ACCEPTED});
    this.ticketService.update(ticketAcceptForm).subscribe(
      res => {
      this.alertService.success(
        this.restaurant.name + ' (番号：' + this.reservedTicket.ticket_type + this.reservedTicket.ticket_no + ' ) に受付しました！', true
      );
        this.router.navigate(['/tickets']);
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
          this.alertService.error(err.error.status_code, 'Invalid input');
        } else {
          this.alertService.error(err.error.status_code, err.error.message);
        }
      }
    );
  }

  cancel() {
    const ticketCancelForm = JSON.stringify({ticket_id: this.reservedTicket.ticket_id, ticket_status: TICKET_STATUS_CANCELLED});
    this.ticketService.update(ticketCancelForm).subscribe(
      res => {
        this.alertService.success(
          this.restaurant.name + ' (番号：' + this.reservedTicket.ticket_type + this.reservedTicket.ticket_no + ' ) の整理券をキャンセルしました！', true
        );
        this.router.navigate(['/tickets']);
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
          this.alertService.error(err.error.status_code, 'Invalid input');
        } else {
          this.alertService.error(err.error.status_code, err.error.message);
        }
      }
    );
  }

  ngOnDestroy() {
    this.alive = false; // switches your IntervalObservable off
  }
}
