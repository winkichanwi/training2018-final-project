import { Component, OnInit } from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import { IRestaurant, RestaurantService } from '../../services/restaurant.service';
import {IUser} from '../../models/user.model';
import {TicketReserve} from '../../models/ticket.model';
import {TicketService} from '../../services/ticket.service';
import {UserService} from '../../services/user.service';
import {AlertService} from '../../services/alert.service';
import {STATUS} from '../../models/status.model';

@Component({
  selector: 'app-ticker-reservation',
  templateUrl: './ticket-reservation.component.html'
})
export class TicketReservationComponent implements OnInit {
  restaurant: IRestaurant = {id: 0, floor: '', name: '', opening_hour: '', cuisine: '', seat_no: 0,
    phone_no: '', status: '', shopping_center_id: 0, image_url: ''};
  user: IUser = {id: 0, full_name: '', email: ''};
  ticket = new TicketReserve(0, 1);
  isSeatNoInvalid = false;
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
    if (this.validateSeatNo()) {
      this.isLoading = true;
      this.reserveTicket();
    }
  }

  private validateSeatNo() {
    this.isSeatNoInvalid = !(this.ticket.seat_no >= 1 && this.ticket.seat_no <= 12);
    return !this.isSeatNoInvalid;
  }

  private reserveTicket() {
    const ticketForm = JSON.stringify(this.ticket);
    this.ticketService.create(ticketForm).subscribe(
      res => {
        this.alertService.success(this.restaurant.name + ' の整理券を取りました！', true);
        this.router.navigate(['/tickets']);
      },
      err => {
        if (err.error instanceof Error) { // browser error
          this.alertService.error(0, err.error.message);
        } else if (err.error.message == null) { // non-customised error
          this.alertService.error(err.status, err.statusText);
        } else if (err.error.status_code === STATUS['DUPLICATED_ENTRY']) { // server error with message not to be shown on UI
          this.alertService.error(err.error.status_code, 'You have already reserved ticket with the same ticket type for this restaurant');
        } else if (err.error.status_code >= STATUS['INTERNAL_SERVER_ERROR']) { // server error with message not to be shown on UI
          this.alertService.error(err.error.status_code, err.statusText);
        } else if (err.error.status_code === STATUS['UNAUTHORIZED']) {
          localStorage.removeItem('authenticated');
          this.alertService.error(err.error.status_code, 'Please login before continue browsing.', true);
          this.router.navigate(['/login'], { queryParams: {returnUrl: this.router.url} });
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
          localStorage.removeItem('authenticated');
          this.alertService.error(err.error.status_code, 'Please login before continue browsing.', true);
          this.router.navigate(['/login'], { queryParams: {returnUrl: this.router.url} });
        } else if (err.error.status_code === STATUS['UNSUPPORTED_FORMAT']) {
          this.alertService.error(err.error.status_code, 'Restaurant not found');
        } else {
          this.alertService.error(err.error.status_code, err.error.message);
        }
      }
    );
  }
}
