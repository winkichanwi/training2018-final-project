import { Component, OnInit } from '@angular/core';
import { AppUtils} from '../app-common';
import { Router } from '@angular/router';
import { UserService, UserLogin  } from '../services/user.service';

interface ILoginResponse {
  result: String;
}

@Component({
  selector: 'app-user-login',
  templateUrl: './user-login.component.html',
  styleUrls: ['./user-login.component.css']
})

export class UserLoginComponent implements OnInit {
  loading = false;
  user = new UserLogin('', '');
  loginJson: any;

  constructor (private router: Router,
               private userService: UserService) { }

  ngOnInit() {}

  onSubmit() {
    this.loading = true;
    this.loginJson = JSON.stringify(this.user);
    this.login();
  }

  private login() {
    this.userService.login(this.loginJson)
      .subscribe(
        (res: ILoginResponse) => {
          if (res.result === 'success') {
            this.router.navigate(['/shopping-centers']);
          }
        },
        err => {
          AppUtils.handleError(err);
          this.loading = false;
        }
      );
  }
}

