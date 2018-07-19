import { Component, OnInit } from '@angular/core';
import { AppUtils} from '../app-common';
import {ActivatedRoute, Router} from '@angular/router';
import {UserLogin} from '../models/user.model';
import {IStatus} from '../models/status.model';
import {AuthService} from '../auth/auth.service';

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

  constructor (private router: Router,
               private authService: AuthService,
               private route: ActivatedRoute) { }

  ngOnInit() {
    // this.authService.logout();
    this.returnUrl = this.route.snapshot.queryParams['returnUrl'] || '/';
  }

  onSubmit() {
    this.isLoading = true;
    this.login();
  }

  private login() {
    const loginJson = JSON.stringify(this.user);
    this.authService.login(loginJson)
      .subscribe(
        (res: IStatus) => {
          if (res.status_code === 2000) {
            // this.authService.authenticate();
            localStorage.setItem(LOCAL_STORAGE_TOKEN, JSON.stringify(true));
            this.router.navigate([this.returnUrl]);
          }
        },
        err => {
          AppUtils.handleError(err);
          this.isLoading = false;
        }
      );
  }
}

