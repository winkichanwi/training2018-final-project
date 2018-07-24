import play.api.http.HttpErrorHandler
import play.api.mvc._
import play.api.mvc.Results._

import scala.concurrent._
import javax.inject.Singleton
import models.{StatusCode, StatusResponse}
import play.api.libs.json.Json;

@Singleton
class ErrorHandler extends HttpErrorHandler {

    def onClientError(request: RequestHeader, statusCode: Int, message: String) = {
        Future.successful(
            Status(statusCode)(Json.toJson(StatusResponse(statusCode*10, message))) // Times 10 to convert to 4 digit
        )
    }

    def onServerError(request: RequestHeader, exception: Throwable) = {
        Future.successful(
            InternalServerError(Json.toJson(StatusResponse(StatusCode.INTERNAL_SERVER_ERROR.code, exception.getMessage)))
        )
    }
}