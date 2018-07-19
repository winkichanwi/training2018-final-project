import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {IStatus} from '../models/status.model';
import {tap} from 'rxjs/internal/operators';
import {Router} from '@angular/router';

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

  constructor(private http: HttpClient, private router: Router) {
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
    return this.http.post(USER_LOGIN_API_ENDPOINT, loginForm, HTTP_OPTIONS)
      .pipe(
        tap (_ =>  localStorage.setItem(LOCAL_STORAGE_TOKEN, JSON.stringify(true)))
        );
  }

  logout() {
    return this.http.post(USER_LOGOUT_API_ENDPOINT, JSON.stringify('logout'), HTTP_OPTIONS)
      .pipe(
        tap(_ => localStorage.removeItem(LOCAL_STORAGE_TOKEN)),
        tap(_ => this.router.navigate(['/']))
        ).subscribe();
  }


  get isLoggedIn(): boolean {
    if (localStorage.getItem(LOCAL_STORAGE_TOKEN)) {
      return true;
    } else {
      return false;
    }
  }
}
