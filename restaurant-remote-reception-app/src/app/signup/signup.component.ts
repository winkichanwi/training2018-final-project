import { Component, OnInit } from '@angular/core';
import { Router} from '@angular/router';
import { AppUtils} from '../app-common';
import { UserService, UserSignup } from '../services/user.service';

interface ISignupResponse {
  result: String;
}

@Component({
  selector: 'app-signup',
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.css']
})

export class SignupComponent implements OnInit {
  loading = false;
  user = new UserSignup('', '', '');
  signupJson: any;

  constructor (private router: Router,
               private userService: UserService) { }

  ngOnInit() {}

  onSubmit() {
    this.loading = true;
    this.signupJson = JSON.stringify(this.user);
    this.signUp();
  }

  private signUp() {
    this.userService.create(this.signupJson)
      .subscribe(
        (res: ISignupResponse) => {
          if (res.result === 'success') {
            this.router.navigate(['/login']);
          }
        },
        err => {
          AppUtils.handleError(err);
          this.loading = false;
        }
      );
  }
}
