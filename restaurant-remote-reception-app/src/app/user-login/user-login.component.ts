import { Component, OnInit } from '@angular/core';
import { AppUtils} from '../app-common';
import { Router } from '@angular/router';
import { UserService} from '../services/user.service';
import {UserLogin} from '../models/user.model';


@Component({
  selector: 'app-user-login',
  templateUrl: './user-login.component.html',
  styleUrls: ['./user-login.component.css']
})

export class UserLoginComponent implements OnInit {
  isLoading = false;
  user = new UserLogin('', '');

  constructor (private router: Router,
               private userService: UserService) { }

  ngOnInit() {}

  onSubmit() {
    this.isLoading = true;
    this.login();
  }

  private login() {
    const loginJson = JSON.stringify(this.user);
    this.userService.login(loginJson)
      .subscribe(
        res => {
            this.router.navigate(['/shopping-centers']);
        },
        err => {
          AppUtils.handleError(err);
          this.isLoading = false;
        }
      );
  }
}

