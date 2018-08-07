import { BrowserModule } from '@angular/platform-browser';
import {ErrorHandler, NgModule} from '@angular/core';
import { HttpClientModule, HttpClientXsrfModule } from '@angular/common/http';
import { FormsModule} from '@angular/forms';
import { AppComponent } from './app.component';

import { SignupComponent } from './components/signup/signup.component';
import { AppRoutingModule} from './app-routing.module';
import { UserLoginComponent } from './components/user-login/user-login.component';
import { ShoppingCenterListComponent } from './components/shopping-center-list/shopping-center-list.component';
import { ShoppingCenterService } from './services/shopping-center.service';

import { RestaurantListComponent } from './components/restaurant-list/restaurant-list.component';
import { RestaurantService } from './services/restaurant.service';
import { RestaurantTicketDisplayPanelComponent } from './components/restaurant-ticket-display-panel/restaurant-ticket-display-panel.component';
import { TicketReservationComponent } from './components/ticket-reservation/ticket-reservation.component';
import { TicketService } from './services/ticket.service';

import { UserService } from './services/user.service';
import { AuthService } from './auth/auth.service';
import { AuthGuard } from './auth/auth.guard';
import { CollapseModule } from 'ngx-bootstrap/collapse';
import { AlertComponent } from './components/alert/alert.component';
<<<<<<< HEAD
import { UserTicketListComponent } from './components/user-ticket-list/user-ticket-list.component';

import { UserTicketItemComponent } from './components/user-ticket-item/user-ticket-item.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RestaurantTicketItemComponent } from './components/restaurant-ticket-item/restaurant-ticket-item.component';
=======
import { CustomErrorHandlerService } from './services/custom-error-handler.service';
>>>>>>> chan/feature/error-handling-cont

@NgModule({
  declarations: [
    AppComponent,
    SignupComponent,
    UserLoginComponent,
    ShoppingCenterListComponent,
    RestaurantListComponent,
    RestaurantTicketDisplayPanelComponent,
    TicketReservationComponent,
    AlertComponent,
    UserTicketListComponent,
    UserTicketItemComponent,
    RestaurantTicketItemComponent
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
    CollapseModule.forRoot(),
    BrowserAnimationsModule
  ],
  providers: [
    CustomErrorHandlerService,
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
