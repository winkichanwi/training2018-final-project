import {ErrorHandler, Injectable} from '@angular/core';
import {AlertService} from './alert.service';
import {HttpErrorResponse} from '@angular/common/http';
import {STATUS} from '../models/status.model';

@Injectable({
  providedIn: 'root'
})
export class CustomErrorHandlerService implements ErrorHandler {

  constructor(private alertService: AlertService) { }

  handleError(error: any,
              duplicatedEntry: string = 'Duplicated entry',
              input: string = 'input',
              resource: string = 'Resource') {
    if (error instanceof HttpErrorResponse) {
      if (error.error.message == null) { // non-customised error
        this.alertService.error(error.status, error.statusText);
      } else if (error.error.status_code === STATUS['DUPLICATED_ENTRY']) {
        this.alertService.error(error.error.status_code,  duplicatedEntry);
      } else if (error.error.status_code >= STATUS['INTERNAL_SERVER_ERROR']) { // server error with message not to be shown on UI
        this.alertService.error(error.error.status_code, error.statusText);
      } else if (error.error.status_code === STATUS['AUTHENTICATION_FAILURE']) { // credentials rejected
        this.alertService.error(error.error.status_code, 'Email address or password incorrect.');
      } else if (error.error.status_code === STATUS['UNAUTHORIZED']) {
        this.alertService.error(error.error.status_code, 'Please login before continue browsing.');
      } else if (error.error.status_code === STATUS['UNSUPPORTED_FORMAT']) {
        this.alertService.error(error.error.status_code, 'Invalid ' + input);
      } else if (error.error.status_code === STATUS['RESOURCE_NOT_FOUND']) {
        this.alertService.error(error.error.status_code, resource + ' not found');
      } else {
        this.alertService.error(error.error.status_code, error.error.message);
      }
    } else {
      this.alertService.error(0, error.message);
    }
    console.error(error.message);
  }
}
