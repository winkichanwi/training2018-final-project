package models

import play.api.libs.functional.syntax._
import play.api.libs.json.{Writes, __}

object StatusCode extends Enumeration {
    protected case class Val(code : Int, message: String) extends super.Val {
        override def toString = message

    }

    implicit def valueToTicketStatusVal(x: Value): Val = x.asInstanceOf[Val]

    val OK = Val(2000, "OK")
    val UNSUPPORTED_FORMAT = Val(4001, "Unsupported Format")
    val UNAUTHORIZED = Val(4010, "Unauthorized")
    val AUTHENTICATION_FAILURE = Val(4011, "Authentication Failure")
    val FORBIDDEN = Val(4030, "Forbidden")
    val RESOURCE_NOT_FOUND = Val(4041, "Resource Not Found")
    val INTERNAL_SERVER_ERROR = Val(5000, "Internal Server Error")
    val DUPLICATED_ENTRY = Val(5001, "Duplicated Entry")
}

case class StatusResponse(status_code: Int, message: String)

object StatusResponse {
    implicit val statusWrites: Writes[StatusResponse] = (
        (__ \ "status_code").write[Int] and
        (__ \ "message").write[String]
    )(unlift(StatusResponse.unapply))
}