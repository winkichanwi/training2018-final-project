package models

import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.mvc._
import play.api.mvc.Results._

import scala.concurrent.{ExecutionContext, Future}

case class ErrorResponse(result: String, message: String)
object ErrorResponse {
    implicit val errorWrites: Writes[ErrorResponse] = (
        (__ \ "result").write[String] and
        (__ \ "message").write[String]
    )(unlift(ErrorResponse.unapply))
}

object Constants {
    val CACHE_TOKEN_USER_ID = "user-id"
    val SUCCESS = "success"
    val FAILURE = "failure"

    val SUCCESS_JSON : JsValue = Json.obj("result" -> SUCCESS)
}

object Utils {
    def resForBadRequest(e: JsError) : Result = {
        val errorResponse = ErrorResponse(Constants.FAILURE, JsError.toJson(e).toString())
        BadRequest(Json.toJson(errorResponse))
    }
}
