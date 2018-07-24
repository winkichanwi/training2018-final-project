import { Component, OnInit, OnDestroy, Input } from '@angular/core';
import { TicketService} from '../services/ticket.service';
import { AppUtils} from '../app-common';
import {interval} from 'rxjs';
import {ITicketCurrentCount, TICKET_TYPES} from '../models/ticket.model';

const intervalCounter = interval(10000);

@Component({
  selector: 'app-ticket-display-panel',
  templateUrl: './ticket-display-panel.component.html',
  styleUrls: ['./ticket-display-panel.component.css']
})
export class TicketDisplayPanelComponent implements OnInit, OnDestroy {
  @Input() restaurantId: number;
  ticketCurrentCounts: ITicketCurrentCount[];
  alive: boolean;

  constructor(private ticketService: TicketService) {
    this.alive = true;
  }

  ngOnInit() {
    this.getTicketCurrentCount();
    intervalCounter.subscribe(() => {
      if (this.alive) {
        this.getTicketCurrentCount();
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
    this.ticketService.getTicketCurrentCount(this.restaurantId).subscribe(
      (res: ITicketCurrentCount[]) => {
        this.ticketCurrentCounts = res;
      },
      err => {
        AppUtils.handleError(err);
      }
    );
  }

  ngOnDestroy() {
    this.alive = false; // switches your IntervalObservable off
  }

}
