import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import {HttpClientModule, HttpClientXsrfModule} from '@angular/common/http';
import { FormsModule} from '@angular/forms';
import { AppComponent } from './app.component';

import { SignupComponent } from './signup/signup.component';
import { AppRoutingModule} from './app-routing.module';
import { UserLoginComponent } from './user-login/user-login.component';
import { ShoppingCenterListComponent } from './shopping-center-list/shopping-center-list.component';
import { ShoppingCenterService } from './services/shopping-center.service';

import { RestaurantListComponent } from './restaurant-list/restaurant-list.component';
import { RestaurantService } from './services/restaurant.service';
import { TicketDisplayPanelComponent } from './ticket-display-panel/ticket-display-panel.component';
import { TicketReservationComponent } from './ticket-reservation/ticket-reservation.component';
import { TicketService } from './services/ticket.service';

import { UserService } from './services/user.service';
import { AuthService } from './auth/auth.service';
import { AuthGuard } from './auth/auth.guard';
import { CollapseModule } from 'ngx-bootstrap/collapse';

@NgModule({
  declarations: [
    AppComponent,
    SignupComponent,
    UserLoginComponent,
    ShoppingCenterListComponent,
    RestaurantListComponent,
    TicketDisplayPanelComponent,
    TicketReservationComponent
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    HttpClientXsrfModule.withOptions({
      cookieName: 'PLAY_SESSION_CSRF',
      headerName: 'Csrf-Token'
    }),
    FormsModule,
    AppRoutingModule,
    CollapseModule.forRoot()
  ],
  providers: [
    ShoppingCenterService,
    RestaurantService,
    TicketService,
    UserService,
    AuthService,
    AuthGuard
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
