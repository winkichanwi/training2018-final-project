import { Component, OnInit, OnDestroy, Input } from '@angular/core';
import {ITicketCount, TICKET_TYPES, TicketService} from '../services/ticket.service';
import { AppUtils} from '../app-common';
import {interval} from 'rxjs';

const intervalCounter = interval(10000);

@Component({
  selector: 'app-ticket-display-panel',
  templateUrl: './ticket-display-panel.component.html',
  styleUrls: ['./ticket-display-panel.component.css']
})
export class TicketDisplayPanelComponent implements OnInit, OnDestroy {
  @Input() restaurantId: number;
  ticketCounts: ITicketCount[];
  alive: boolean;

  constructor(private ticketService: TicketService) {
    this.alive = true;
  }

  ngOnInit() {
    this.getTicketCount();
    intervalCounter.subscribe(() => {
      if (this.alive) {
        this.getTicketCount();
      }
    });
  }

  private ticketTypeSeatLabel(type: String): String {
    for (const typeEntry of TICKET_TYPES) {
      if (typeEntry.type === type) {
        return typeEntry.min_seat_no + '-' + typeEntry.max_seat_no;
      }
    }
  }

  private getTicketCount() {
    this.ticketService.getTicketCount(this.restaurantId).subscribe(
      (res: ITicketCount[]) => {
        this.ticketCounts = res;
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
