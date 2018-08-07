import { Component, OnInit } from '@angular/core';
import { Router} from '@angular/router';
import { UserService} from '../../services/user.service';
import {UserSignup} from '../../models/user.model';
import {IStatus, STATUS} from '../../models/status.model';
import {AlertService} from '../../services/alert.service';
import {CustomErrorHandlerService} from '../../services/custom-error-handler.service';

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
               private alertService: AlertService,
               private errorHandler: CustomErrorHandlerService) { }

  ngOnInit() {}

  onSubmit() {
    if (!this.invalidPwdLen()) {
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
        res => {
            this.alertService.success('登録成功！ログインしてください。', true);
            this.router.navigate(['/login']);
        },
        err => {
          this.errorHandler.handleError(err, 'Email address (' + this.user.email + ') has already been registered.');
          this.isLoading = false;
        }
      );
  }
}
