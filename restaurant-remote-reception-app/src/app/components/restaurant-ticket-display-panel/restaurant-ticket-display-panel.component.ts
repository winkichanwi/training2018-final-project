import { Component, OnInit, OnDestroy, Input } from '@angular/core';
import { TicketService} from '../../services/ticket.service';
import {interval} from 'rxjs';
import {IReservedTicket, IRestaurantTicketCount, TICKET_TYPES} from '../../models/ticket.model';
import {AlertService} from '../../services/alert.service';
import {STATUS} from '../../models/status.model';
import {Router} from '@angular/router';

const intervalCounter = interval(5000);

@Component({
  selector: 'app-ticket-display-panel',
  templateUrl: './restaurant-ticket-display-panel.component.html',
  styleUrls: ['./restaurant-ticket-display-panel.component.css']
})
export class RestaurantTicketDisplayPanelComponent implements OnInit, OnDestroy {
  @Input() restaurantId: number;
  ticketCurrentCounts: IRestaurantTicketCount[] = [];
  hasReservedTickets = false;
  reservedTickets: IReservedTicket[] = [];
  alive: boolean;

  constructor(private ticketService: TicketService,
              private alertService: AlertService,
              private router: Router) {
    this.alive = true;
  }

  ngOnInit() {
    this.getReservedTickets();
    if (this.hasReservedTickets) {
      intervalCounter.subscribe(() => {
        if (this.alive) {
          this.getReservedTickets();
        }
      });
    } else {
      this.getTicketCurrentCount();
      intervalCounter.subscribe(() => {
        if (this.alive) {
          this.getTicketCurrentCount();
        }
      });
    }
  }

  ticketTypeSeatLabel(type: String): String {
    for (const typeEntry of TICKET_TYPES) {
      if (typeEntry.type === type) {
        return typeEntry.min_seat_no + '-' + typeEntry.max_seat_no;
      }
    }
  }

  private getTicketCurrentCount() {
    this.ticketService.getRestaurantTicketCounts(this.restaurantId).subscribe(
      (res: IRestaurantTicketCount[]) => {
        this.ticketCurrentCounts = res;
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
        } else {
          this.alertService.error(err.error.status_code, err.error.message);
        }
      }
    );
  }

  private getReservedTickets(): any {
    this.ticketService.getRestaurantReservedTicket(this.restaurantId).subscribe(
      (res: IReservedTicket[]) => {
        this.hasReservedTickets = res.length > 0;
        this.reservedTickets = res;
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
