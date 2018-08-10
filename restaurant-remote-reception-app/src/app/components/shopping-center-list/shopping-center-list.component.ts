import { Component, OnInit } from '@angular/core';
import {IShoppingCenter, ShoppingCenterService} from '../../services/shopping-center.service';
import {AlertService} from '../../services/alert.service';
import {Router} from '@angular/router';
import {CustomErrorHandlerService} from '../../services/custom-error-handler.service';

@Component({
  selector: 'app-shopping-centers',
  templateUrl: './shopping-center-list.component.html',
  styleUrls: ['./shopping-center-list.component.css']
})

export class ShoppingCenterListComponent implements OnInit {
  shoppingCenters: IShoppingCenter[];

  constructor (private shoppingCenterService: ShoppingCenterService,
               private alertService: AlertService,
               private router: Router,
               private errorHandler: CustomErrorHandlerService) {}

  ngOnInit() {
    this.getListOfShoppingCenters();
  }

  private getListOfShoppingCenters() {
    this.shoppingCenterService.getList().subscribe(
      (res: IShoppingCenter[]) => {
        this.shoppingCenters = res;
      },
      err => {
        this.errorHandler.handleError(err, '', '', 'List of shopping centers', this.router.url);
      }
    );
  }

}
