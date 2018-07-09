import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {environment} from '../../environments/environment';

const SHOPPING_CENTERS_API_ENDPOINT = environment.API_HOST + '/api/shopping-centers';

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
    return this.http.get(SHOPPING_CENTERS_API_ENDPOINT);
  }

  public getInfo(shoppingCenterId: string) {
    return this.http.get(SHOPPING_CENTERS_API_ENDPOINT + '/' + shoppingCenterId);
  }
}
