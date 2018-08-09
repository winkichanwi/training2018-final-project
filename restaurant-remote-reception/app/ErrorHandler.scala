import play.api.http.HttpErrorHandler
import play.api.mvc._
import play.api.mvc.Results._

import scala.concurrent._
import javax.inject.Singleton
import models.{StatusCode, StatusResponse}
import play.api.libs.json.Json;
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException

@Singleton
class ErrorHandler extends HttpErrorHandler {

    def onClientError(request: RequestHeader, statusCode: Int, message: String) = {
        Future.successful(
            Status(statusCode)(Json.toJson(StatusResponse(statusCode*10, message))) // Times 10 to convert to 4 digit
        )
    }

    def onServerError(request: RequestHeader, exception: Throwable) = {
        exception match {
            case e: MySQLIntegrityConstraintViolationException =>
                Future.successful(
                    InternalServerError(Json.toJson(StatusResponse(StatusCode.DUPLICATED_ENTRY.code, StatusCode.DUPLICATED_ENTRY.message)))
                )
            case e =>
                Future.successful(
                    InternalServerError(Json.toJson(StatusResponse(StatusCode.INTERNAL_SERVER_ERROR.code, e.getMessage)))
                )
        }
    }
}