import { Component, OnInit } from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import { IRestaurant, RestaurantService } from '../../services/restaurant.service';
import {IUser} from '../../models/user.model';
import {TicketReserve} from '../../models/ticket.model';
import {TicketService} from '../../services/ticket.service';
import {UserService} from '../../services/user.service';
import {AlertService} from '../../services/alert.service';
import {STATUS} from '../../models/status.model';
import {CustomErrorHandlerService} from '../../services/custom-error-handler.service';

@Component({
  selector: 'app-ticker-reservation',
  templateUrl: './ticket-reservation.component.html'
})
export class TicketReservationComponent implements OnInit {
  restaurant: IRestaurant;
  user: IUser;
  ticket = new TicketReserve(0, 1);
  isSeatNoInvalid = false;
  isLoading = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private restaurantService: RestaurantService,
    private userService: UserService,
    private ticketService: TicketService,
    private alertService: AlertService,
    private errorHandler: CustomErrorHandlerService
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
        this.errorHandler.handleError(err, '同じ種類の整理券はもう取りましたよ！');
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

          this.errorHandler.handleError(err);
        }
      );
    }

  private getRestaurantInfo() {
    this.restaurantService.getInfo(this.ticket.restaurant_id.toString()).subscribe(
      (res: IRestaurant) => {
        this.restaurant = res;
      },
      err => {
        this.errorHandler.handleError(err, '', '', 'Restaurant', this.router.url);
      }
    );
  }
}
