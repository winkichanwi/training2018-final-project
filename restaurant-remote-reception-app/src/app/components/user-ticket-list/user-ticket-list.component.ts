import {Component, OnDestroy, OnInit} from '@angular/core';
import {IReservedTicket} from '../../models/ticket.model';
import {AlertService} from '../../services/alert.service';
import {TicketService} from '../../services/ticket.service';
import {STATUS} from '../../models/status.model';
import {NavigationEnd, Router} from '@angular/router';

@Component({
  selector: 'app-user-ticket-list',
  templateUrl: './user-ticket-list.component.html',
  styleUrls: ['./user-ticket-list.component.css']
})
export class UserTicketListComponent implements OnInit, OnDestroy {
  reservedTickets: IReservedTicket[];
  navigationSubscription;

  constructor(private ticketService: TicketService,
              private router: Router,
              private alertService: AlertService) {
    this.navigationSubscription = this.router.events.subscribe((e: any) => {
      // If it is a NavigationEnd event re-initalise the component
      if (e instanceof NavigationEnd) {
        this.initialise();
      }
    });
  }

  ngOnInit() {}

  initialise() {
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
    // avoid memory leaks here by cleaning up after ourselves. If we
    // don't then we will continue to run our initialiseInvites()
    // method on every navigationEnd event.
    if (this.navigationSubscription) {
      this.navigationSubscription.unsubscribe();
    }
  }
}
