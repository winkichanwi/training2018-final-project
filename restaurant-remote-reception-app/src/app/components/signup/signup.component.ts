import { Component, OnInit } from '@angular/core';
import { Router} from '@angular/router';
import { UserService} from '../../services/user.service';
import {UserSignup} from '../../models/user.model';
import {IStatus, STATUS} from '../../models/status.model';
import {AlertService} from '../../services/alert.service';

@Component({
  selector: 'app-signup',
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.css']
})

export class SignupComponent implements OnInit {
  isLoading = false;
  user = new UserSignup('', '', '');
  isPwdLenInvalid = false;

  constructor (private router: Router,
               private userService: UserService,
               private alertService: AlertService) { }

  ngOnInit() {}

  onSubmit() {
    if(!this.invalidPwdLen()) {
      this.isLoading = true;
      this.signUp();
    }
  }

  private invalidPwdLen() {
    return this.isPwdLenInvalid = !(this.user.password.length >= 8 && this.user.password.length <= 20);
  }

  private signUp() {
    const signupJson = JSON.stringify(this.user);
    this.userService.create(signupJson)
      .subscribe(
        (res: IStatus) => {
          if (res.status_code === STATUS['OK']) {
            this.alertService.success('登録成功！ログインしてください。', true);
            this.router.navigate(['/login']);
          }
        },
        err => {
          if (err.error instanceof Error) { // browser error
            this.alertService.error(0, err.error.message);
          } else if (err.error.message == null) { // non-customised error
            this.alertService.error(err.status, err.statusText);
          } else if (err.error.status_code === STATUS['DUPLICATED_ENTRY']) { // server error with message not to be shown on UI
            this.alertService.error(err.error.status_code,  'Email address (' + this.user.email + ') has already been registered.');
          } else if (err.error.status_code >= STATUS['INTERNAL_SERVER_ERROR']) { // server error with message not to be shown on UI
            this.alertService.error(err.error.status_code, err.statusText);
          } else if (err.error.status_code === STATUS['UNSUPPORTED_FORMAT']) {
            this.alertService.error(err.error.status_code, 'Invalid input');
          } else {
            this.alertService.error(err.error.status_code, err.error.message);
          }
          this.isLoading = false;
        }
      );
  }
}
