import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {environment} from '../../environments/environment';

export interface IRestaurant {
  id: number;
  name: string;
  shopping_center_id: number;
  status: string;
  floor: string;
  seat_no: number;
  cuisine: string;
  phone_no: string;
  opening_hour: string;
  image_url: string;
}

@Injectable({
  providedIn: 'root'
})

export class RestaurantService {
  static genRestaurantsListApiEndpoint(shoppingCenterId: string) {
    return environment.API_HOST + '/api/shopping-centers/' + shoppingCenterId + '/restaurants';
  }

  static genRestaurantApiEndpoint(restaurantId: string) {
    return environment.API_HOST + '/api/restaurants/' + restaurantId;
  }

  constructor(private http: HttpClient) { }

  public getList(shoppingCenterId: string) {
    return this.http.get(RestaurantService.genRestaurantsListApiEndpoint(shoppingCenterId));
  }

  public getInfo(restaurantId: string) {
    return this.http.get(RestaurantService.genRestaurantApiEndpoint(restaurantId));
  }
}
