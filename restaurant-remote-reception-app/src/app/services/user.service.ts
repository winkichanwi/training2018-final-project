import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';

const USERS_API_ENDPOINT = '/api/users';
const CURRENT_USER_API_ENDPOINT = '/api/users/me';

const HTTP_OPTIONS = {
  headers: new HttpHeaders({
    'Content-Type':  'application/json; charset=utf-8',
  })
};

@Injectable({
  providedIn: 'root'
})
export class UserService {
  constructor(private http: HttpClient) { }

  public create(signupForm: any) {
    return this.http.post(USERS_API_ENDPOINT, signupForm, HTTP_OPTIONS);
  }

  public getCurrentUserInfo() {
    return this.http.get(CURRENT_USER_API_ENDPOINT);
  }
}
