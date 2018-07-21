import { Component, OnInit } from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import { IRestaurant, RestaurantService } from '../../services/restaurant.service';
import {IUser} from '../../models/user.model';
import {Ticket} from '../../models/ticket.model';
import {TicketService} from '../../services/ticket.service';
import {UserService} from '../../services/user.service';
import {AlertService} from '../../services/alert.service';
import {STATUS} from '../../models/status.model';

@Component({
  selector: 'app-ticker-reservation',
  templateUrl: './ticket-reservation.component.html',
  styleUrls: ['./ticket-reservation.component.css']
})
export class TicketReservationComponent implements OnInit {
  restaurant: IRestaurant;
  user: IUser;
  ticket = new Ticket(0, 0, 1, 'Active');
  seatNoIsInvalid = false;
  isLoading = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private restaurantService: RestaurantService,
    private userService: UserService,
    private ticketService: TicketService,
    private alertService: AlertService
  ) { }

  ngOnInit() {
    this.ticket.restaurant_id = parseInt(this.route.snapshot.paramMap.get('restaurantId'), 10);
    this.getRestaurantInfo();
    this.getUserInfo();
  }

  onSubmit() {
    if(this.validateSeatNo()) {
      this.isLoading = true;
      this.reserveTicket();
    }
  }

  private validateSeatNo() {
    this.seatNoIsInvalid = !(this.ticket.seat_no >= 1 && this.ticket.seat_no <= 12);
    return !this.seatNoIsInvalid;
  }

  private reserveTicket() {
    const ticketForm = JSON.stringify(this.ticket);
    this.ticketService.create(ticketForm).subscribe(
      (res: Response) => {
        this.router.navigate(['/shopping-centers', this.restaurant.shopping_center_id]);
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
        } else if (err.error.status_code === STATUS['UNSUPPORTED_FORMAT']) {
          this.alertService.error(err.error.status_code, 'Invalid input');
        } else {
          this.alertService.error(err.error.status_code, err.error.message);
        }
        this.isLoading = false;
      }
    );
  }

  private getUserInfo() {
      this.userService.getCurrentUserInfo().subscribe(
        (res: IUser) => {
          this.user = res;
          this.ticket.created_by_id = this.user.id;
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

  private getRestaurantInfo() {
    this.restaurantService.getInfo(this.ticket.restaurant_id.toString()).subscribe(
      (res: IRestaurant) => {
        this.restaurant = res;
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
        } else if (err.error.status_code === STATUS['UNSUPPORTED_FORMAT']) {
          this.alertService.error(err.error.status_code, 'Restaurant not found');
        } else {
          this.alertService.error(err.error.status_code, err.error.message);
        }
      }
    );
  }
}
