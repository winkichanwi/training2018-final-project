import { Component, OnInit } from '@angular/core';
import {Router} from '@angular/router';
import {HttpClient} from '@angular/common/http';
import {environment} from '../../environments/environment';

interface ShoppingCenters {
  id: number;
  name: string;
  branch: string;
}

@Component({
  selector: 'app-shopping-centers',
  templateUrl: './shopping-centers.component.html',
  styleUrls: ['./shopping-centers.component.css']
})

export class ShoppingCentersComponent implements OnInit {
  getUrl = environment.apiUrl + '/shopping-centers';
  shoppingCenters: ShoppingCenters[];

  constructor (private http: HttpClient, private router: Router) {
    this.http.get(this.getUrl).subscribe((res: ShoppingCenters[]) => {
      this.shoppingCenters = res;
    });
  }

  ngOnInit() {
  }

}
