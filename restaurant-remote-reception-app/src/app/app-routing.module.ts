import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { SignupComponent} from './signup/signup.component';
import { UserLoginComponent } from './user-login/user-login.component';
import {ShoppingCenterListComponent} from './shopping-center-list/shopping-center-list.component';
import {RestaurantListComponent} from './restaurant-list/restaurant-list.component';

const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full'},
  { path: 'signup', component: SignupComponent },
  { path: 'login', component: UserLoginComponent },
  { path: 'shopping-centers', component: ShoppingCenterListComponent },
  { path: 'shopping-centers/:shoppingCenterId', component: RestaurantListComponent }
];

@NgModule({
  exports: [ RouterModule ],
  imports: [
    RouterModule.forRoot(routes)
  ]
})

export class AppRoutingModule {}
