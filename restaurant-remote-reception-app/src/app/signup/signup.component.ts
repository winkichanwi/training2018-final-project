import { Component, OnInit } from '@angular/core';
import { UserSignup } from './user-signup';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { Router} from '@angular/router';

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
  signupForm: FormGroup;
  user = new UserSignup('', '', '');
  signupJson = '';
  postUrl = environment.apiUrl + '/users';

  constructor (private http: HttpClient, private router: Router) { }


  ngOnInit() {
    this.signupForm = new FormGroup({
      'inputFullname': new FormControl(this.user.full_name, Validators.required),
      'inputEmail': new FormControl(this.user.email, [
        Validators.required,
        Validators.email
      ]),
      'inputPassword': new FormControl(this.user.password, [
        Validators.required,
        Validators.minLength(6)
      ]),

    });
  }

  onSubmit() {
    this.signupJson = JSON.stringify(this.user);
    this.http.post<SignupResponse>(this.postUrl, this.signupJson, httpOptions)
      .subscribe(
        res => {
          if (res.result === 'success') {
            this.router.navigate(['/home']);
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
