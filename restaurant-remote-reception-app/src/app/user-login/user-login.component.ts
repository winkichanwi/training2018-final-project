import { Component, OnInit } from '@angular/core';
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {HttpClient, HttpErrorResponse, HttpHeaders} from '@angular/common/http';
import {environment} from '../../environments/environment';
import {Router} from '@angular/router';
import {UserLogin} from './user-login';
import {AppComponent} from '../app.component';

const httpOptions = {
  headers: new HttpHeaders({
    'Content-Type':  'application/signupJson',
  })
};

interface LoginResponse {
  result: String;
  full_name: String;
}

@Component({
  selector: 'app-user-login',
  templateUrl: './user-login.component.html',
  styleUrls: ['./user-login.component.css'],
  providers: [AppComponent]
})

export class UserLoginComponent implements OnInit {
  loginForm: FormGroup;
  user = new UserLogin('', '');
  loginJson = '';
  postUrl = environment.apiUrl + '/users/authentication';
  sessionToken: String;

  constructor (private http: HttpClient, private router: Router) { }

  ngOnInit() {
    this.loginForm = new FormGroup({
      'inputEmail': new FormControl(this.user.email, [
        Validators.required,
        Validators.email
      ]),
      'inputPassword': new FormControl(this.user.password, [
        Validators.required,
        Validators.minLength(6),
        Validators.maxLength(20)
      ]),
    });
  }

  onSubmit() {
    this.loginJson = JSON.stringify(this.user);
    this.http.post<LoginResponse>(this.postUrl, this.loginJson, httpOptions)
      .subscribe(
        res => {
          if (res.result === 'success') {
            this.router.navigate(['/shopping-centers']);
          }
        },
        (err: HttpErrorResponse) => {
          if (err.error instanceof Error) {
            console.log('Client-side error occured.');
          } else {
            console.log('Server-side error occured.');
          }
        }
      );
  }
}

