import { Component, OnInit } from '@angular/core';
import {IReservedTicket} from '../../models/ticket.model';
import {AlertService} from '../../services/alert.service';
import {TicketService} from '../../services/ticket.service';
import {STATUS} from '../../models/status.model';

@Component({
  selector: 'app-user-ticket-list',
  templateUrl: './user-ticket-list.component.html',
  styleUrls: ['./user-ticket-list.component.css']
})
export class UserTicketListComponent implements OnInit {
  reservedTickets: IReservedTicket[];

  constructor(private ticketService: TicketService,
              private alertService: AlertService) {}

  ngOnInit() {
    this.getReservedTickets();
  }

  private getReservedTickets() {
    this.ticketService.getReservedTickets().subscribe(
      (res: IReservedTicket[]) => {
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
          this.alertService.error(err.error.status_code, 'Please login');
        } else {
          this.alertService.error(err.error.status_code, err.error.message);
        }
      }
    );
  }
}
