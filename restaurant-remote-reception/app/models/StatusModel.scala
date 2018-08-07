package models

import play.api.libs.functional.syntax._
import play.api.libs.json.{Writes, __}

sealed abstract class StatusCode(val code: Int, val message: String)

object StatusCode {
    case object OK extends StatusCode(2000, "OK")
    case object UNSUPPORTED_FORMAT extends StatusCode(4001, "Unsupported Format")
    case object UNAUTHORIZED extends StatusCode(4010, "Unauthorized")
    case object AUTHENTICATION_FAILURE extends StatusCode(4011, "Authentication Failure")
    case object FORBIDDEN extends StatusCode(4030, "Forbidden")
    case object RESOURCE_NOT_FOUND extends StatusCode(4041, "Resource Not Found")
    case object INTERNAL_SERVER_ERROR extends StatusCode(5000, "Internal Server Error")
    case object DUPLICATED_ENTRY extends StatusCode(5001, "Duplicated Entry")
}

case class StatusResponse(status_code: Int, message: String)

object StatusResponse {
    implicit val statusWrites: Writes[StatusResponse] = (
        (__ \ "status_code").write[Int] and
        (__ \ "message").write[String]
    )(unlift(StatusResponse.unapply))
}
