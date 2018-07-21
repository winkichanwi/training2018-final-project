import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {IStatus} from '../models/status.model';
import {Router} from '@angular/router';
import {AlertService} from '../services/alert.service';

const USER_LOGIN_API_ENDPOINT = '/api/users/login';
const USER_LOGOUT_API_ENDPOINT = '/api/users/logout';
const USER_AUTHENTICATION_API_ENDPOINT = '/api/users/authentication';
const LOCAL_STORAGE_TOKEN = 'authenticated';

const HTTP_OPTIONS = {
  headers: new HttpHeaders({
    'Content-Type':  'application/json',
  })
};

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  constructor(private http: HttpClient,
              private router: Router,
              private alertService: AlertService) {
    this.authenticate();
  }

  authenticate() {
    this.http.get(USER_AUTHENTICATION_API_ENDPOINT).subscribe(
      (status: IStatus) => {
        if (status.status_code === 2000) {
          localStorage.setItem('authenticated', JSON.stringify(true));
        }
      },
      err => {
        localStorage.removeItem('authenticated');
      }
    );
  }

  login(loginForm: any) {
    return this.http.post(USER_LOGIN_API_ENDPOINT, loginForm, HTTP_OPTIONS);
  }

  logout() {
    return this.http.post(USER_LOGOUT_API_ENDPOINT, JSON.stringify('logout'), HTTP_OPTIONS);
  }


  get isLoggedIn(): boolean {
    if (localStorage.getItem(LOCAL_STORAGE_TOKEN)) {
      return true;
    } else {
      return false;
    }
  }
}
