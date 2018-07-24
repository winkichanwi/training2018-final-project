import { Component, OnInit, OnDestroy, Input } from '@angular/core';
import { TicketService} from '../../services/ticket.service';
import {interval} from 'rxjs';
import {ITicketCurrentCount, TICKET_TYPES} from '../../models/ticket.model';
import {AlertService} from '../../services/alert.service';
import {STATUS} from '../../models/status.model';

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

  constructor(private ticketService: TicketService,
              private alertService: AlertService) {
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
        if (err.error instanceof Error) { // browser error
          this.alertService.error(0, err.error.message);
        } else if (err.error.message == null) { // non-customised error
          this.alertService.error(err.status, err.statusText);
        } else if (err.error.status_code >= STATUS['INTERNAL_SERVER_ERROR']) { // server error with message not to be shown on UI
          this.alertService.error(err.error.status_code, err.statusText);
        } else if (err.error.status_code === STATUS['UNAUTHORIZED']) {
          this.alertService.error(err.error.status_code, 'Please login');
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
