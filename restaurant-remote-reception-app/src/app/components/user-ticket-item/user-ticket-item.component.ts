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
import {CustomErrorHandlerService} from '../../services/custom-error-handler.service';

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
  restaurant: IRestaurant;
  shoppingCenter: IShoppingCenter;
  restaurantLastCalled: IRestaurantLastCalled;
  alive: boolean;
  flashState = 'inactive';

  constructor(private shoppingCenterService: ShoppingCenterService,
              private restaurantService: RestaurantService,
              private ticketService: TicketService,
              private alertService: AlertService,
              private router: Router,
              private errorHandler: CustomErrorHandlerService) {
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
        this.errorHandler.handleError(err, '', '', 'Restaurant', this.router.url);
      }
    );
  }

  private getShoppingCenterInfo() {
    this.shoppingCenterService.getInfo(this.restaurant.shopping_center_id.toString()).subscribe(
      (res: IShoppingCenter) => {
        this.shoppingCenter = res;
      },
      err => {
        this.errorHandler.handleError(err, '', '', 'Shopping center', this.router.url);
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
        this.errorHandler.handleError(err, '', '', '', this.router.url);
        // TODO: initialise as 0 at API side
        // } else if (err.error.status_code === STATUS['RESOURCE_NOT_FOUND']) {
        //   this.restaurantLastCalled = { ticket_type: this.reservedTicket.ticket_type, last_called: 0 };
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
        this.errorHandler.handleError(err, '', '', '', this.router.url);
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
        this.errorHandler.handleError(err, '', '', '', this.router.url);
      }
    );
  }

  ngOnDestroy() {
    this.alive = false; // switches your IntervalObservable off
  }
}
