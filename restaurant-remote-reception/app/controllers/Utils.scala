package controllers

import models.Constants
import models.Tables.Users
import play.api.mvc.Request
import slick.driver.MySQLDriver.api._

object Utils {
    def getSessionUser(rs: Request[Any]) = {
        val sessionUserId = rs.session.get(Constants.SESSION_TOKEN_USER_ID).getOrElse("0")
        Users.filter(t => t.userId === sessionUserId.toInt).result.headOption
    }
}
