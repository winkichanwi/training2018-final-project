import {HttpErrorResponse} from '@angular/common/http';

export class AppUtils {
  public static handleError(err: HttpErrorResponse) {
    if (err.error instanceof Error) {
      console.log('Client-side error occured:' + err.error.message);
    } else if (err.error.message == null) {
      console.log('Server-side error occured:' + err.statusText);
    } else {
      console.log(err.error.message);
    }
  }
}
