import { Injectable } from '@angular/core';
import {environment} from '../../environments/environment';
import {HttpClient} from '@angular/common/http';

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
  getListUrl = '';

  constructor(private http: HttpClient) { }

  public getList(shoppingCenterId: string) {
    this.getListUrl = environment.apiUrl + '/shopping-centers/' + shoppingCenterId + '/restaurants';
    return this.http.get(this.getListUrl);
  }
}
