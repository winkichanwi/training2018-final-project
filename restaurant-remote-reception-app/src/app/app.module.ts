import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { HttpClientModule} from '@angular/common/http';
import { FormsModule} from '@angular/forms';

import { AppComponent } from './app.component';
import { SignupComponent } from './signup/signup.component';
import { AppRoutingModule} from './app-routing.module';
import { HomeComponent } from './home/home.component';
import { UserLoginComponent } from './user-login/user-login.component';
import { ShoppingCenterListComponent } from './shopping-center-list/shopping-center-list.component';

import { ShoppingCenterService } from './services/shopping-center.service';
import {RestaurantListComponent} from './restaurant-list/restaurant-list.component';
import {RestaurantService} from './services/restaurant.service';

@NgModule({
  declarations: [
    AppComponent,
    SignupComponent,
    HomeComponent,
    UserLoginComponent,
    ShoppingCenterListComponent,
    RestaurantListComponent
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    FormsModule,
    AppRoutingModule
  ],
  providers: [
    ShoppingCenterService,
    RestaurantService],
  bootstrap: [AppComponent]
})
export class AppModule { }
