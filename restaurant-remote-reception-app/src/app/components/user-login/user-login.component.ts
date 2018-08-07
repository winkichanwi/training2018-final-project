import { Component, OnInit } from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {UserLogin} from '../../models/user.model';
import {IStatus, STATUS} from '../../models/status.model';
import {AuthService} from '../../auth/auth.service';
import {AlertService} from '../../services/alert.service';
import {CustomErrorHandlerService} from '../../services/custom-error-handler.service';

const LOCAL_STORAGE_TOKEN = 'authenticated';

@Component({
  selector: 'app-user-login',
  templateUrl: './user-login.component.html',
  styleUrls: ['./user-login.component.css']
})

export class UserLoginComponent implements OnInit {
  isLoading = false;
  user = new UserLogin('', '');
  returnUrl: String;
  isPwdLenInvalid = false;

  constructor (private router: Router,
               private authService: AuthService,
               private route: ActivatedRoute,
               private alertService: AlertService,
               private errorHandler: CustomErrorHandlerService) { }

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
          this.errorHandler.handleError(err, '', 'email address or password.');
          this.isLoading = false;
        }
      );
  }
}

