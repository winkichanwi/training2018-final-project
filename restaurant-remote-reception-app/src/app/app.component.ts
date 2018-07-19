import {Component, OnInit} from '@angular/core';
import {AuthService} from './auth/auth.service';
import {AppUtils} from './app-common';
import {IStatus} from './models/status.model';
import {Router} from '@angular/router';

const LOCAL_STORAGE_TOKEN = 'authenticated';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  title = 'レストレ';

  constructor(public authService: AuthService, private router: Router) { }

  ngOnInit() {
  }

  logout() {
    this.authService.logout().subscribe(
      (res: IStatus) => {
        if (res.status_code === 2000) {
          localStorage.removeItem(LOCAL_STORAGE_TOKEN);
          this.router.navigate(['/login']);
        }
      }
    );
  }
}
