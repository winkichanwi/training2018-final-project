import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {environment} from '../../environments/environment';

export interface IShoppingCenter {
  id: number;
  name: string;
  branch: string;
}

@Injectable({
  providedIn: 'root'
})

export class ShoppingCenterService {
  getListUrl = environment.apiUrl + '/shopping-centers';
  getInfoUrl = '';

  constructor(private http: HttpClient) { }

  public getList() {
    return this.http.get(this.getListUrl);
  }

  public getInfo(shoppingCenterId: string) {
    this.getInfoUrl = environment.apiUrl + '/shopping-centers/' + shoppingCenterId;
    return this.http.get(this.getInfoUrl);
  }
}
