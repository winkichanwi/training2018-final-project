import { Component, OnInit, OnDestroy, Input } from '@angular/core';
import { TicketService} from '../../services/ticket.service';
import {interval} from 'rxjs';
import {IReservedTicket, IRestaurantTicketCount, IRestaurantTicketCountResponse, TICKET_TYPES} from '../../models/ticket.model';
import {AlertService} from '../../services/alert.service';
import {STATUS} from '../../models/status.model';
import {Router} from '@angular/router';
import {CustomErrorHandlerService} from '../../services/custom-error-handler.service';

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
              private router: Router,
              private errorHandler: CustomErrorHandlerService) {
    this.alive = true;
  }

  ngOnInit() {
    this.getReservedTickets();
    if (!this.hasReservedTickets) {
      this.getTicketCurrentCount();
    }
    intervalCounter.subscribe(() => {
      if (this.alive) {
        this.getReservedTickets();
      }
    });
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
      (res: IRestaurantTicketCountResponse) => {
        this.ticketCurrentCounts = res.ticket_counts;
      },
      err => {
        this.errorHandler.handleError(err, '', '', '', this.router.url);
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
      this.errorHandler.handleError(err, '', '', '', this.router.url);
    }
    );
  }

  ngOnDestroy() {
    this.alive = false; // switches your IntervalObservable off
  }

}
