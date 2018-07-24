import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { SignupComponent} from './signup/signup.component';
import { UserLoginComponent } from './user-login/user-login.component';
import {ShoppingCenterListComponent} from './shopping-center-list/shopping-center-list.component';
import {RestaurantListComponent} from './restaurant-list/restaurant-list.component';
import {TicketReservationComponent} from './ticket-reservation/ticket-reservation.component';
import {AuthGuard} from './auth/auth.guard';
import {AuthService} from './auth/auth.service';

const routes: Routes = [
  { path: '', redirectTo: 'shopping-centers', pathMatch: 'full'},
  { path: 'signup', component: SignupComponent },
  { path: 'login', component: UserLoginComponent },
  { path: 'shopping-centers', component: ShoppingCenterListComponent, canActivate: [AuthGuard]},
  { path: 'shopping-centers/:shoppingCenterId', component: RestaurantListComponent, canActivate: [AuthGuard]},
  { path: 'restaurants/:restaurantId/ticket-reservation', component: TicketReservationComponent, canActivate: [AuthGuard]},
  // otherwise redirect to home
  { path: '**', redirectTo: ''}
  ];

@NgModule({
  exports: [ RouterModule ],
  imports: [
    RouterModule.forRoot(routes)
  ],
  providers: [AuthGuard, AuthService]
})
export class AppRoutingModule {}
