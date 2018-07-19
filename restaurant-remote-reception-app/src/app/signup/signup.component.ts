import { Component, OnInit } from '@angular/core';
import { Router} from '@angular/router';
import { AppUtils} from '../app-common';
import { UserService} from '../services/user.service';
import {UserSignup} from '../models/user.model';
import {IStatus} from '../models/status.model';

interface ISignupResponse {
  result: String;
}

@Component({
  selector: 'app-signup',
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.css']
})

export class SignupComponent implements OnInit {
  isLoading = false;
  user = new UserSignup('', '', '');

  constructor (private router: Router,
               private userService: UserService) { }

  ngOnInit() {}

  onSubmit() {
    this.isLoading = true;
    this.signUp();
  }

  private signUp() {
    const signupJson = JSON.stringify(this.user);
    this.userService.create(signupJson)
      .subscribe(
        (res: IStatus) => {
          if (res.status_code === 2000) {
            this.router.navigate(['/login']);
          }
        },
        err => {
          AppUtils.handleError(err);
          this.isLoading = false;
        }
      );
  }
}
