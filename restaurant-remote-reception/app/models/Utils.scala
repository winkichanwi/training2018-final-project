package models

import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.mvc._
import play.api.mvc.Results._

import scala.concurrent.{ExecutionContext, Future}

case class ErrorResponse(result: String, errorMessage: String)
object ErrorResponse {
    implicit val errorWrites: Writes[ErrorResponse] = (
        (__ \ "result").write[String] and
        (__ \ "error_message").write[String]
    )(unlift(ErrorResponse.unapply))
}

object Constants {
    val success = "success"
    val failure = "failure"

    val successJson : JsValue = Json.obj("result" -> success)
}

object Utils {
    def resForBadRequest(e: JsError) : Result = {
        val errorResponse = ErrorResponse(Constants.failure, JsError.toJson(e).toString())
        BadRequest(Json.toJson(errorResponse))
    }
}
