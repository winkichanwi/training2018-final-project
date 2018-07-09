import { Component, OnInit } from '@angular/core';
import { UserSignup } from '../models/user-signup';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Router} from '@angular/router';
import { AppUtils} from '../app-common';
import {environment} from '../../environments/environment';

const httpOptions = {
  headers: new HttpHeaders({
    'Content-Type':  'application/json',
  })
};

const USERS_API_ENDPOINT = environment.API_HOST + '/api/users';

interface SignupResponse {
  result: String;
}

@Component({
  selector: 'app-signup',
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.css']
})

export class SignupComponent implements OnInit {
  loading = false;
  user = new UserSignup('', '', '');
  signupJson: any;

  constructor (private http: HttpClient, private router: Router) { }

  ngOnInit() {}

  onSubmit() {
    this.loading = true;
    this.signupJson = JSON.stringify(this.user);
    this.signUp();
  }

  private signUp() {
    this.http.post(USERS_API_ENDPOINT, this.signupJson, httpOptions)
      .subscribe(
        (res: SignupResponse) => {
          if (res.result === 'success') {
            this.router.navigate(['/login']);
          }
        },
        err => {
          AppUtils.handleError(err);
          this.loading = false;
        }
      );
  }
}
