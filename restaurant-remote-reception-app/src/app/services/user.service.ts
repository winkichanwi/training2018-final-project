import {Injectable} from '@angular/core';
import {environment} from '../../environments/environment';
import {HttpClient, HttpHeaders} from '@angular/common/http';

const USERS_API_ENDPOINT = environment.API_HOST + '/api/users';
const CURRENT_USER_API_ENDPOINT = environment.API_HOST + '/api/me';
const USER_AUTHENTICATION_API_ENDPOINT = environment.API_HOST + '/api/users/authentication';

const httpOptions = {
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
    return this.http.post(USERS_API_ENDPOINT, signupForm, httpOptions);
  }

  public login(loginForm: any) {
    return this.http.post(USER_AUTHENTICATION_API_ENDPOINT, loginForm, httpOptions);
  }

  public getMe() {
    return this.http.get(CURRENT_USER_API_ENDPOINT);
  }
}
