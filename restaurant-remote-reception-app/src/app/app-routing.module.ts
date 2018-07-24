import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { SignupComponent} from './components/signup/signup.component';
import { UserLoginComponent } from './components/user-login/user-login.component';
import {ShoppingCenterListComponent} from './components/shopping-center-list/shopping-center-list.component';
import {RestaurantListComponent} from './components/restaurant-list/restaurant-list.component';
import {TicketReservationComponent} from './components/ticket-reservation/ticket-reservation.component';
import {AuthGuard} from './auth/auth.guard';
import {AuthService} from './auth/auth.service';
import {UserTicketListComponent} from './components/user-ticket-list/user-ticket-list.component';

const routes: Routes = [
  { path: '', redirectTo: 'shopping-centers', pathMatch: 'full'},
  { path: 'signup', component: SignupComponent },
  { path: 'login', component: UserLoginComponent },
  { path: 'shopping-centers',
    component: ShoppingCenterListComponent,
    canActivate: [AuthGuard]
  },
  { path: 'shopping-centers/:shoppingCenterId',
    component: RestaurantListComponent,
    canActivate: [AuthGuard],
  },
  { path: 'restaurants/:restaurantId/ticket-reservation',
    component: TicketReservationComponent,
    canActivate: [AuthGuard],
  },
  { path: 'tickets',
    component: UserTicketListComponent,
    canActivate: [AuthGuard],
    runGuardsAndResolvers: 'always'
  },
  // otherwise redirect to home
  { path: '**', redirectTo: ''}
  ];

@NgModule({
  exports: [ RouterModule ],
  imports: [
    RouterModule.forRoot(routes, {onSameUrlNavigation: 'reload'})
  ],
  providers: [AuthGuard, AuthService]
})
export class AppRoutingModule {}
