import { Component, OnInit } from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import { IRestaurant, RestaurantService } from '../services/restaurant.service';
import { AppUtils } from '../app-common';
import {IUser} from '../models/user.model';
import {Ticket} from '../models/ticket.model';
import {TicketService} from '../services/ticket.service';
import {UserService} from '../services/user.service';

@Component({
  selector: 'app-ticker-reservation',
  templateUrl: './ticket-reservation.component.html',
  styleUrls: ['./ticket-reservation.component.css']
})
export class TicketReservationComponent implements OnInit {
  restaurant: IRestaurant;
  user: IUser;
  ticket = new Ticket(0, 0, 1, 'Active');
  isLoading = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private restaurantService: RestaurantService,
    private userService: UserService,
    private ticketService: TicketService
  ) { }

  ngOnInit() {
    this.ticket.restaurant_id = parseInt(this.route.snapshot.paramMap.get('restaurantId'), 10);
    this.getRestaurantInfo();
    this.getUserInfo();
  }

  onSubmit() {
    this.reserveTicket();
  }

  private reserveTicket() {
    const ticketForm = JSON.stringify(this.ticket);
    this.ticketService.create(ticketForm).subscribe(
      res => {
        this.router.navigate(['/shopping-centers', this.restaurant.shopping_center_id]);
      },
      err => {
        AppUtils.handleError(err);
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
          AppUtils.handleError(err);
        }
      );
    }

  private getRestaurantInfo() {
    this.restaurantService.getInfo(this.ticket.restaurant_id.toString()).subscribe(
      (res: IRestaurant) => {
        this.restaurant = res;
      },
      err => {
          AppUtils.handleError(err);
      }
    );
  }
}
