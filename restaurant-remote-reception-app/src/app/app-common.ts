import { environment } from '../environments/environment';
import {HttpErrorResponse} from '@angular/common/http';

export class AppSettings {
  public static USERS_API_ENDPOINT = environment.API_HOST + '/api/users';
  public static USER_AUTHENTICATION_API_ENDPOINT = environment.API_HOST + '/api/users/authentication';
  public static SHOPPING_CENTERS_API_ENDPOINT = environment.API_HOST + '/api/shopping-centers';
  public static RESTAURANTS_API_ENDPOINT(shoppingCenterId: string) {
    return this.SHOPPING_CENTERS_API_ENDPOINT + '/' + shoppingCenterId + '/restaurants';
  }
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
