import {Component, OnInit} from '@angular/core';
import { UserSignup } from './user-signup';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import {HttpClient, HttpHeaders} from '@angular/common/http';

const httpOptions = {
  headers: new HttpHeaders({
    'Content-Type':  'application/json',
  })
};

@Component({
  selector: 'app-signup',
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.css']
})

export class SignupComponent implements OnInit {
  signupForm: FormGroup;
  user = new UserSignup('', '', '');
  json = '';
  apiHost = 'http://localhost:9000';
  postUrl = this.apiHost + '/users';

  constructor (private http: HttpClient) { }

  ngOnInit() {
    this.signupForm = new FormGroup({
      'inputFullname': new FormControl(this.user.full_name, Validators.required),
      'inputEmail': new FormControl(this.user.full_name, [
        Validators.required,
        Validators.email
      ]),
      'inputPassword': new FormControl(this.user.full_name, [
        Validators.required,
        Validators.minLength(6),
        Validators.maxLength(20)
      ]),

    });
  }

  onSubmit() {
    this.json = JSON.stringify(this.user);
    this.http.post(this.postUrl, this.json, httpOptions).subscribe( res => console.log(res));
  }
}
