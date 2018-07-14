import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { IRestaurant, RestaurantService } from '../services/restaurant.service';
import { AppUtils } from '../app-common';
import { IUser, UserService } from '../services/user.service';

@Component({
  selector: 'app-ticker-reservation',
  templateUrl: './ticker-reservation.component.html',
  styleUrls: ['./ticker-reservation.component.css']
})
export class TickerReservationComponent implements OnInit {
  restaurantId: string;
  restaurant: IRestaurant;
  user: IUser;
  seatNo = 1;
  loading = false;

  constructor(
    private route: ActivatedRoute,
    private restaurantService: RestaurantService,
    private userService: UserService
  ) { }

  ngOnInit() {
    this.restaurantId = this.route.snapshot.paramMap.get('restaurantId');
    this.getRestaurantInfo();
    this.getUserInfo();
  }

  onSubmit() {

  }

  private getUserInfo() {
      this.userService.getInfo().subscribe(
        (res: IUser) => {
          this.user = res;
        },
        err => {
          AppUtils.handleError(err);
        }
      );
    }

  private getRestaurantInfo() {
    this.restaurantService.getInfo(this.restaurantId).subscribe(
      (res: IRestaurant) => {
        this.restaurant = res;
      },
      err => {
          AppUtils.handleError(err);
      }
    );
  }
}
