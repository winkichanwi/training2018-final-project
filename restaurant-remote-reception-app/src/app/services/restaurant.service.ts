import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {AppSettings} from '../app-common';

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
  constructor(private http: HttpClient) { }

  public getList(shoppingCenterId: string) {
    return this.http.get(AppSettings.RESTAURANTS_API_ENDPOINT(shoppingCenterId));
  }
}
