import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {AppSettings} from '../app-common';

export interface IShoppingCenter {
  id: number;
  name: string;
  branch: string;
}

@Injectable({
  providedIn: 'root'
})

export class ShoppingCenterService {
  constructor(private http: HttpClient) { }

  public getList() {
    return this.http.get(AppSettings.SHOPPING_CENTERS_API_ENDPOINT);
  }

  public getInfo(shoppingCenterId: string) {
    return this.http.get(AppSettings.SHOPPING_CENTERS_API_ENDPOINT + '/' + shoppingCenterId);
  }
}
