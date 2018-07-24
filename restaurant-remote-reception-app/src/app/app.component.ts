import {Component, OnInit} from '@angular/core';
import {AuthService} from './auth/auth.service';
import {Router} from '@angular/router';

const LOCAL_STORAGE_TOKEN = 'authenticated';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  title = 'レストレ';
  navbarOpen = false;

  constructor(public authService: AuthService, private router: Router) { }

  ngOnInit() {
  }

  toggleNavbar() {
    this.navbarOpen = !this.navbarOpen;
  }

  logout() {
    this.authService.logout().subscribe(
      res => {
          localStorage.removeItem(LOCAL_STORAGE_TOKEN);
          this.router.navigate(['/login']);
      }
    );
  }
}
