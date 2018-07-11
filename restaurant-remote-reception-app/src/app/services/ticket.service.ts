import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {environment} from '../../environments/environment';

export interface ITicketCount {
  ticket_type: string;
  ticket_count: string;
}

export interface ITicketLastNo {
  ticket_type: string;
  last_called: number;
  last_active: number;
}

export const TICKET_TYPES = [
  { type: 'A', min_seat_no: 1, max_seat_no: 2},
  { type: 'B', min_seat_no: 3, max_seat_no: 5},
  { type: 'C', min_seat_no: 6, max_seat_no: 8},
  { type: 'D', min_seat_no: 9, max_seat_no: 12},
];

export const TICKET_STATUS = [
  { status: 'Active', is_active: true },
  { status: 'Called', is_active: true },
  { status: 'Cancelled', is_active: false }, // cancelled by either customer or restaurant
  { status: 'Accepted', is_active: false }, // accepted by restaurant
  { status: 'Archived', is_active: false }, // archive when restaurant closes
];

@Injectable({
  providedIn: 'root'
})
export class TicketService{
  static genRestaurantTicketApiEndpoint(restaurantId: number) {
    return environment.API_HOST + '/api/restaurants/' + restaurantId + '/tickets';
  }

  constructor(private http: HttpClient) { }

  public getTicketCount(restaurantId: number) {
    return this.http.get(TicketService.genRestaurantTicketApiEndpoint(restaurantId));
  }

  public getTicketLastNo(restaurantId: number) {
    return this.http.get(TicketService.genRestaurantTicketApiEndpoint(restaurantId));
  }
}
