import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {AppSettings} from '../app-common';
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
  static genRestaurantsApiEndpoint(shoppingCenterId: string) {
    return environment.API_HOST + '/api/shopping-centers/' + shoppingCenterId + '/restaurants';
  }

  constructor(private http: HttpClient) { }

  public getList(shoppingCenterId: string) {
    return this.http.get(RestaurantService.genRestaurantsApiEndpoint(shoppingCenterId));
  }
}
