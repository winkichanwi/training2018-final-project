import { Component, OnInit } from '@angular/core';
import { Router} from '@angular/router';
import { AppUtils} from '../../app-common';
import { UserService} from '../../services/user.service';
import {UserSignup} from '../../models/user.model';
import {IStatus} from '../../models/status.model';
import {AlertService} from '../../services/alert.service';

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
               private userService: UserService,
               private alertService: AlertService) { }

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
          if (err.error instanceof Error) { // browser error
            this.alertService.error(0, err.error.message);
          } else if (err.error.message == null) { // non-customised error
            this.alertService.error(err.status, err.statusText);
          } else if (err.error.status_code === 5001 ) { // server error with message not to be shown on UI
            this.alertService.error(err.error.status_code,  ' Email address (' + this.user.email + ') has already been registered.');
          } else if (err.error.status_code >= 5000 ) { // server error with message not to be shown on UI
            this.alertService.error(err.error.status_code, err.statusText);
          } else {
            this.alertService.error(err.error.status_code, err.error.message);
          }
          this.isLoading = false;
        }
      );
  }
}
