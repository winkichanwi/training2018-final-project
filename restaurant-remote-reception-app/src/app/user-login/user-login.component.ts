import { Component, OnInit } from '@angular/core';
import {HttpClient, HttpErrorResponse, HttpHeaders} from '@angular/common/http';
import {AppSettings} from '../app-common';
import {Router} from '@angular/router';
import {UserLogin} from '../models/user-login';

const httpOptions = {
  headers: new HttpHeaders({
    'Content-Type':  'application/json',
  })
};

interface LoginResponse {
  result: String;
  full_name: String;
}

@Component({
  selector: 'app-user-login',
  templateUrl: './user-login.component.html',
  styleUrls: ['./user-login.component.css']
})

export class UserLoginComponent implements OnInit {
  user = new UserLogin('', '');
  loginJson = '';
  sessionToken: String;

  constructor (private http: HttpClient, private router: Router) { }

  ngOnInit() {}

  onSubmit() {
    this.loginJson = JSON.stringify(this.user);
    this.http.post(AppSettings.USER_AUTHENTICATION_API_ENDPOINT, this.loginJson, httpOptions)
      .subscribe(
        (res: LoginResponse) => {
          if (res.result === 'success') {
            this.router.navigate(['/shopping-centers']);
          }
        },
        err => {
          if (err.error instanceof Error) {
            console.log('Client-side error occured.');
          } else {
            console.log(err.error.message);
          }
        }
      );
  }
}

