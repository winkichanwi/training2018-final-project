package security

import models.{Constants, ID}
import play.api.mvc.Request

trait SessionImplicits {
    implicit def request2SessionUserId(implicit request: Request[_]): ID = ID(request.session.get(Constants.SESSION_TOKEN_USER_ID).getOrElse("0").toInt)
}
