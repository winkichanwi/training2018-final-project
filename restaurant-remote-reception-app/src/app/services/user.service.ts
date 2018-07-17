import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';

const USERS_API_ENDPOINT = '/api/users';
const USER_API_ENDPOINT = '/api/me';
const USER_AUTHENTICATION_API_ENDPOINT = '/api/users/authentication';

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
  constructor(private http: HttpClient) { }

  public create(signupForm: any) {
    return this.http.post(USERS_API_ENDPOINT, signupForm, httpOptions);
  }

  public login(loginForm: any) {
    return this.http.post(USER_AUTHENTICATION_API_ENDPOINT, loginForm, httpOptions);
  }

  public getInfo() {
    return this.http.get(USER_API_ENDPOINT);
  }
}
