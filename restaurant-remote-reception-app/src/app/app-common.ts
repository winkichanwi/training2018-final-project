import { environment } from '../environments/environment';

export class AppSettings {
  public static USERS_API_ENDPOINT = environment.API_HOST + '/api/users';
  public static USER_AUTHENTICATION_API_ENDPOINT = environment.API_HOST + '/api/users/authentication';
  public static SHOPPING_CENTERS_API_ENDPOINT = environment.API_HOST + '/api/shopping-centers';
  public static RESTAURANTS_API_ENDPOINT(shoppingCenterId: string) {
    return this.SHOPPING_CENTERS_API_ENDPOINT + '/' + shoppingCenterId + '/restaurants';
  }
}
