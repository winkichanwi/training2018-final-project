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
import {CustomErrorHandlerService} from '../../services/custom-error-handler.service';

const intervalCounter = interval(5000);
const animationIntervalCounter = interval(500);

@Component({
  selector: 'app-restaurant-ticket-item',
  templateUrl: './restaurant-ticket-item.component.html',
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
export class RestaurantTicketItemComponent implements OnInit, OnDestroy {
  @Input() reservedTicket: IReservedTicket;
  restaurantLastCalled: IRestaurantLastCalled = {ticket_type: '', last_called: 0};
  alive: boolean;
  flashState = 'inactive';

  constructor(private ticketService: TicketService,
              private router: Router,
              private errorHandler: CustomErrorHandlerService) {
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
        this.errorHandler.handleError(err, '', '', '', this.router.url);
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
