import { environment } from '../environments/environment';
import {HttpErrorResponse} from '@angular/common/http';

export class AppSettings {
  public static USERS_API_ENDPOINT = environment.API_HOST + '/api/users';
  public static USER_AUTHENTICATION_API_ENDPOINT = environment.API_HOST + '/api/users/authentication';
  public static SHOPPING_CENTERS_API_ENDPOINT = environment.API_HOST + '/api/shopping-centers';
  public static RESTAURANTS_API_ENDPOINT(shoppingCenterId: string) {
    return this.SHOPPING_CENTERS_API_ENDPOINT + '/' + shoppingCenterId + '/restaurants';
  }
  public static RESTAURANT_TICKET_API_ENDPOINT(restaurantId: number) {
    return environment.API_HOST + '/api/restaurants/' + restaurantId + '/tickets';
  }
}

export class AppConstants {
  public static TICKET_TYPES = [
    { type: 'A', min_seat_no: 1, max_seat_no: 2},
    { type: 'B', min_seat_no: 3, max_seat_no: 5},
    { type: 'C', min_seat_no: 6, max_seat_no: 8},
    { type: 'D', min_seat_no: 9, max_seat_no: 12},
  ];

  public static TICKET_STATUS = [
    { status: 'Active', is_active: true },
    { status: 'Called', is_active: true },
    { status: 'Cancelled', is_active: false },
    { status: 'Closed', is_active: false },
  ];
}

export class AppUtils {
  public static handleError(err: HttpErrorResponse) {
    if (err.error instanceof Error) {
      console.log('Client-side error occured:' + err.error.message);
    } else if (err.error.message == null) {
      console.log('Server-side error occured:' + err.statusText);
    } else {
      console.log(err.error.message);
    }
  }
}
