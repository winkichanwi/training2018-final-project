import {Component, OnDestroy, OnInit} from '@angular/core';
import {IReservedTicket} from '../../models/ticket.model';
import {AlertService} from '../../services/alert.service';
import {TicketService} from '../../services/ticket.service';
import {STATUS} from '../../models/status.model';
import {NavigationEnd, Router} from '@angular/router';
import {CustomErrorHandlerService} from '../../services/custom-error-handler.service';

@Component({
  selector: 'app-user-ticket-list',
  templateUrl: './user-ticket-list.component.html'
})
export class UserTicketListComponent implements OnInit, OnDestroy {
  reservedTickets: IReservedTicket[];
  navigationSubscription;

  constructor(private ticketService: TicketService,
              private router: Router,
              private errorHandler: CustomErrorHandlerService) {
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
        this.errorHandler.handleError(err, '', '', '', this.router.url);
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
