import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';

const TICKETS_API_ENDPOINT = '/api/tickets';
const RESERVED_TICKETS_API_ENDPOINT = '/api/tickets/me';

const HTTP_OPTIONS = {
  headers: new HttpHeaders({
    'Content-Type':  'application/json; charset=utf-8',
  })
};

@Injectable({
  providedIn: 'root'
})
export class TicketService {
  static genRestaurantTicketCountsApiEndpoint(restaurantId: number) {
    return '/api/restaurants/' + restaurantId + '/tickets/count';
  }

  static genRestaurantLastCalledApiEndpoint(restaurantId: number) {
    return '/api/restaurants/' + restaurantId + '/tickets/last-called';
  }

  static genRestaurantReservedTicketsApiEndpoint(restaurantId: number) {
    return '/api/restaurants/' + restaurantId + '/tickets/me';
  }

  constructor(private http: HttpClient) { }

  public create(ticketForm: any) {
    return this.http.post(TICKETS_API_ENDPOINT, ticketForm, HTTP_OPTIONS);
  }

  public update(ticketForm: any) {
    return this.http.put(TICKETS_API_ENDPOINT, ticketForm, HTTP_OPTIONS);
  }

  public getRestaurantTicketCounts(restaurantId: number) {
    return this.http.get(TicketService.genRestaurantTicketCountsApiEndpoint(restaurantId));
  }

  public getReservedTickets() {
    return this.http.get(RESERVED_TICKETS_API_ENDPOINT);
  }

  public getRestaurantLastCalled(restaurantId: number) {
    return this.http.get(TicketService.genRestaurantLastCalledApiEndpoint(restaurantId));
  }

  public getRestaurantReservedTicket(restaurantId: number) {
    return this.http.get(TicketService.genRestaurantReservedTicketsApiEndpoint(restaurantId));
  }
}
