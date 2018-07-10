import { Injectable } from '@angular/core';
import {environment} from '../../environments/environment';
import {HttpClient, HttpHeaders} from '@angular/common/http';

const USERS_API_ENDPOINT = environment.API_HOST + '/api/users';
const USER_AUTHENTICATION_API_ENDPOINT = environment.API_HOST + '/api/users/authentication';

const httpOptions = {
  headers: new HttpHeaders({
    'Content-Type':  'application/json',
  })
};

export interface IUser {
  id: number;
  full_name: string;
  email: string;
}

export class UserSignup {
  constructor(
    public full_name: string,
    public email: string,
    public password: string
  ) { }
}

export class UserLogin {
  constructor(
    public email: string,
    public password: string
  ) { }
}

@Injectable({
  providedIn: 'root'
})
export class UserService {
  static genUserApiEndpoint(userId: string) {
    return USERS_API_ENDPOINT + '/' + userId;
  }

  constructor(private http: HttpClient) { }

  public create(signupForm: any) {
    return this.http.post(USERS_API_ENDPOINT, signupForm, httpOptions);
  }

  public login(loginForm: any) {
    return this.http.post(USER_AUTHENTICATION_API_ENDPOINT, loginForm, httpOptions);
  }

  public getInfo(userId: string) {
    return this.http.get(UserService.genUserApiEndpoint(userId));
  }
}
