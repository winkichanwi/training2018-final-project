import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {interval} from 'rxjs';
import {IReservedTicket, IRestaurantLastCalled} from '../../models/ticket.model';
import {AlertService} from '../../services/alert.service';
import {TicketService} from '../../services/ticket.service';
import {STATUS} from '../../models/status.model';
import {
  trigger,
  state,
  style
} from '@angular/animations';
import {Router} from '@angular/router';

const intervalCounter = interval(5000);
const animationIntervalCounter = interval(500);

@Component({
  selector: 'app-restaurant-ticket-item',
  templateUrl: './restaurant-ticket-item.component.html',
  styleUrls: ['./restaurant-ticket-item.component.css'],
  animations: [
    trigger('flashState', [
      state('inactive',
        style({backgroundColor: '#E4F1FB'})
      ),
      state('active',
        style({backgroundColor: '#FFD2C0'})
      ),
    ])
  ]
})
export class RestaurantTicketItemComponent implements OnInit, OnDestroy {
  @Input() reservedTicket: IReservedTicket;
  restaurantLastCalled: IRestaurantLastCalled;
  alive: boolean;
  flashState = 'inactive';

  constructor(private ticketService: TicketService,
              private router: Router,
              private alertService: AlertService) {
    this.alive = true;
  }

  ngOnInit() {
    this.getRestaurantLastCalled();
    intervalCounter.subscribe(() => {
      if (this.alive) {
        this.getRestaurantLastCalled(this.restaurantLastCalled.last_called);
      }
    });
  }

  private getRestaurantLastCalled(originalLastCalled = -1) {
    this.ticketService.getRestaurantLastCalled(this.reservedTicket.restaurant_id, this.reservedTicket.ticket_type).subscribe(
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
          localStorage.removeItem('authenticated');
          this.alertService.error(err.error.status_code, 'Please login before continue browsing.', true);
          this.router.navigate(['/login'], { queryParams: {returnUrl: this.router.url} });
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

  ngOnDestroy() {
    this.alive = false;
  }

}
