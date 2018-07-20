import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';

const USERS_API_ENDPOINT = '/api/users';
const CURRENT_USER_API_ENDPOINT = '/api/me';
const USER_AUTHENTICATION_API_ENDPOINT = '/api/users/authentication';

const HTTP_OPTIONS = {
  headers: new HttpHeaders({
    'Content-Type':  'application/json',
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

  public login(loginForm: any) {
    return this.http.post(USER_AUTHENTICATION_API_ENDPOINT, loginForm, HTTP_OPTIONS);
  }

  public getCurrentUserInfo() {
    return this.http.get(CURRENT_USER_API_ENDPOINT);
  }
}
