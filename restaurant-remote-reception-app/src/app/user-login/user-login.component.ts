import { Component, OnInit } from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import { AppUtils} from '../app-common';
import {Router} from '@angular/router';
import {UserLogin} from '../models/user-login';
import {environment} from '../../environments/environment';

const httpOptions = {
  headers: new HttpHeaders({
    'Content-Type':  'application/json',
  })
};

const USER_AUTHENTICATION_API_ENDPOINT = environment.API_HOST + '/api/users/authentication';

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
  loading = false;
  user = new UserLogin('', '');
  loginJson: any;
  sessionToken: String;

  constructor (private http: HttpClient, private router: Router) { }

  ngOnInit() {}

  onSubmit() {
    this.loading = true;
    this.loginJson = JSON.stringify(this.user);
    this.login();
  }

  private login() {
    this.http.post(USER_AUTHENTICATION_API_ENDPOINT, this.loginJson, httpOptions)
      .subscribe(
        (res: LoginResponse) => {
          if (res.result === 'success') {
            this.router.navigate(['/shopping-centers']);
          }
        },
        err => {
          AppUtils.handleError(err);
          this.loading = false;
        }
      );
  }
}

