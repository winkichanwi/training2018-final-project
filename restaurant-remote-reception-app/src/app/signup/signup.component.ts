import { Component, OnInit } from '@angular/core';
import { UserSignup } from '../models/user-signup';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { Router} from '@angular/router';
import { AppSettings} from '../app-common';

const httpOptions = {
  headers: new HttpHeaders({
    'Content-Type':  'application/json',
  })
};

interface SignupResponse {
  result: String;
}

@Component({
  selector: 'app-signup',
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.css']
})

export class SignupComponent implements OnInit {
  user = new UserSignup('', '', '');
  signupJson = '';

  constructor (private http: HttpClient, private router: Router) { }


  ngOnInit() {}

  onSubmit() {
    this.signupJson = JSON.stringify(this.user);
    this.http.post(AppSettings.USERS_API_ENDPOINT, this.signupJson, httpOptions)
      .subscribe(
        (res: SignupResponse) => {
          if (res.result === 'success') {
            this.router.navigate(['/login']);
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
