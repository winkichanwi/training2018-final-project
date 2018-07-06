import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import {IShoppingCenter, ShoppingCenterService} from '../services/shopping-center.service';

@Component({
  selector: 'app-restaurant-list',
  templateUrl: './restaurant-list.component.html',
  styleUrls: ['./restaurant-list.component.css']
})
export class RestaurantListComponent implements OnInit {
  shoppingCenterId: string;
  shoppingCenter: IShoppingCenter;

  constructor( private route: ActivatedRoute, private shoppingCenterService: ShoppingCenterService ) {
    this.shoppingCenterId = this.route.snapshot.paramMap.get('shoppingCenterId');
    this.shoppingCenterService.getInfo(this.shoppingCenterId).subscribe((res: IShoppingCenter) => {
      this.shoppingCenter = res;
    });
  }

  ngOnInit() {
  }

}
