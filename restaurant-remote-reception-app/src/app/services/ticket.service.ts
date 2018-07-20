import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';

const TICKETS_API_ENDPOINT = '/api/tickets';

const HTTP_OPTIONS = {
  headers: new HttpHeaders({
    'Content-Type':  'application/json',
  })
};

@Injectable({
  providedIn: 'root'
})
export class TicketService {
  static genRestaurantTicketCountsApiEndpoint(restaurantId: number) {
    return '/api/restaurants/' + restaurantId + '/tickets';
  }

  constructor(private http: HttpClient) { }

  public getTicketCurrentCount(restaurantId: number) {
    return this.http.get(TicketService.genRestaurantTicketCountsApiEndpoint(restaurantId));
  }

  public create(ticketForm: any) {
    return this.http.post(TICKETS_API_ENDPOINT, ticketForm, HTTP_OPTIONS);
  }
}
