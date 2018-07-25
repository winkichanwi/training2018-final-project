import { Component, OnInit } from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {UserLogin} from '../../models/user.model';
import {IStatus, STATUS} from '../../models/status.model';
import {AuthService} from '../../auth/auth.service';
import {AlertService} from '../../services/alert.service';

const LOCAL_STORAGE_TOKEN = 'authenticated';

@Component({
  selector: 'app-user-login',
  templateUrl: './user-login.component.html'
})

export class UserLoginComponent implements OnInit {
  isLoading = false;
  user = new UserLogin('', '');
  returnUrl: String;
  isPwdLenInvalid = false;

  constructor (private router: Router,
               private authService: AuthService,
               private route: ActivatedRoute,
               private alertService: AlertService) { }

  ngOnInit() {
    this.returnUrl = this.route.snapshot.queryParams['returnUrl'] || '/';
    if (this.authService.isLoggedIn) {
      this.alertService.success('You have already logged in.', true);
      this.router.navigate([this.returnUrl]);
    }
  }

  onSubmit() {
    if (!this.invalidPwdLen()) {
      this.isLoading = true;
      this.login();
    }
  }

  invalidPwdLen() {
    return this.isPwdLenInvalid = !(this.user.password.length >= 8 && this.user.password.length <= 20);
  }

  private login() {
    const loginJson = JSON.stringify(this.user);
    this.authService.login(loginJson)
      .subscribe(
        res => {
            localStorage.setItem(LOCAL_STORAGE_TOKEN, JSON.stringify(true));
            this.router.navigate([this.returnUrl]);
        },
        err => {
          if (err.error instanceof Error) { // browser error
            this.alertService.error(0, err.error.message);
          } else if (err.error.message == null) { // non-customised error
            this.alertService.error(err.status, err.statusText);
          } else if (err.error.status_code >= STATUS['INTERNAL_SERVER_ERROR']) { // server error with message not to be shown on UI
            this.alertService.error(err.error.status_code, err.statusText);
          } else if (err.error.status_code === STATUS['AUTHENTICATION_FAILURE']) { // credentials rejected
            this.alertService.error(err.error.status_code, 'Email address or password incorrect.');
          } else if (err.error.status_code === STATUS['UNSUPPORTED_FORMAT']) {
            this.alertService.error(err.error.status_code, 'Invalid email address or password.');
          } else {
            this.alertService.error(err.error.status_code, err.error.message);
          }
          this.isLoading = false;
        }
      );
  }
}

