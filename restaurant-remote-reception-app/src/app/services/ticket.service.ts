import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {AppSettings} from '../app-common';

export interface ITicketCount {
  ticket_type: string;
  ticket_count: string;
}

@Injectable({
  providedIn: 'root'
})
export class TicketService {

  constructor(private http: HttpClient) { }

  public getTicketCount(restaurantId: number) {
    return this.http.get(AppSettings.RESTAURANT_TICKET_API_ENDPOINT(restaurantId));
  }
}
