import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { IRestaurant, RestaurantService } from '../services/restaurant.service';
import { AppUtils } from '../app-common';
import { IUser, UserService } from '../services/user.service';
import { CookieService } from 'ngx-cookie-service';

@Component({
  selector: 'app-ticker-reservation',
  templateUrl: './ticker-reservation.component.html',
  styleUrls: ['./ticker-reservation.component.css']
})
export class TickerReservationComponent implements OnInit {
  sessionToken: string;
  restaurantId: string;
  restaurant: IRestaurant;
  userId = '1';
  user: IUser;
  seatNo = 0;
  loading = false;

  constructor(
    private route: ActivatedRoute,
    private restaurantService: RestaurantService,
    private userService: UserService,
    private cookieService: CookieService
  ) { }

  ngOnInit() {
    this.sessionToken = this.cookieService.get('user-id');
    console.log('Session');
    console.log(this.sessionToken);
    this.restaurantId = this.route.snapshot.paramMap.get('restaurantId');
    this.getRestaurantInfo();
    this.getUserInfo();
  }

  onSubmit() {
  }

  private getUserInfo() {
      this.userService.getInfo(this.userId).subscribe(
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
