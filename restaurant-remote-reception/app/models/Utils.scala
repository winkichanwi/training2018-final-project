package models

import play.api.libs.json.{Writes, __}
import play.api.libs.functional.syntax._

class Utils {
    case class Error(result: String, errorMessage: String)

    implicit val errorWrites: Writes[Error] = (
        (__ \ "result").write[String] and
        (__ \ "error_message").write[String]
    )(unlift(Error.unapply))
}
