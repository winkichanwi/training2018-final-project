import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';

export interface ITicketCurrentCount {
  ticket_type: string;
  ticket_count: string;
}

export const TICKET_TYPES = [
  { type: 'A', min_seat_no: 1, max_seat_no: 2},
  { type: 'B', min_seat_no: 3, max_seat_no: 5},
  { type: 'C', min_seat_no: 6, max_seat_no: 8},
  { type: 'D', min_seat_no: 9, max_seat_no: 12},
];

export const TICKET_STATUS = [
  { status: 'Active', is_waiting: true },
  { status: 'Called', is_waiting: true },
  { status: 'Cancelled', is_waiting: false }, // cancelled by either customer or restaurant
  { status: 'Accepted', is_waiting: false }, // accepted by restaurant
  { status: 'Archived', is_waiting: false }, // archive when restaurant closes
];

@Injectable({
  providedIn: 'root'
})
export class TicketService{
  static genRestaurantTicketCountsApiEndpoint(restaurantId: number) {
    return '/api/restaurants/' + restaurantId + '/tickets';
  }

  constructor(private http: HttpClient) { }

  public getTicketCurrentCount(restaurantId: number) {
    return this.http.get(TicketService.genRestaurantTicketCountsApiEndpoint(restaurantId));
  }
}
