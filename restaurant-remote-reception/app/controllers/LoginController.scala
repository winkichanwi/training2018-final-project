package controllers

import play.api.mvc._
import play.api.db.slick._
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile
import slick.driver.MySQLDriver.api._
import models.Tables._
import javax.inject.Inject
import models.{Constants, StatusCode, StatusResponse}

import scala.concurrent.{ExecutionContext, Future}
import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._

class LoginController @Inject()(val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext)
    extends Controller with HasDatabaseConfigProvider[JdbcProfile]  {

    import LoginController._

    def login = Action.async(parse.json) { implicit rs =>
        rs.body.validate[LoginForm].map { form =>
            val loginEmail = form.email
            val loginPassword = form.password

            val queryUserInfoSecretDBIO = Users.join(UserSecret).on(_.userId === _.userId)
                .filter { case (t1, t2) =>
                    t1.email === loginEmail
                }.result.headOption

            val authenFailRes = StatusResponse(StatusCode.AUTHENTICATION_FAILURE.code, StatusCode.AUTHENTICATION_FAILURE.message)
            db.run(queryUserInfoSecretDBIO).map {
                case Some((user, userSecret)) =>
                    if (userSecret.password == loginPassword) {
                        Ok(Json.toJson(StatusResponse(StatusCode.OK.code, StatusCode.OK.message)))
                            .withSession(Constants.CACHE_TOKEN_USER_ID -> user.userId.toString())
                    } else {
                        BadRequest(Json.toJson(authenFailRes))
                    }
                case None =>
                    BadRequest(Json.toJson(authenFailRes))
            }
        }.recoverTotal { e =>
            Future { BadRequest(Json.toJson(StatusResponse(StatusCode.UNSUPPORTED_FORMAT.code, StatusCode.UNSUPPORTED_FORMAT.message)))}
        }
    }

    def authorize = Action.async { implicit rs =>
        val authorizedUserDBIO = for {
            sessionUserId <- rs.session.get(Constants.CACHE_TOKEN_USER_ID)
            userDBIO <- Users.filter(t => t.userId === sessionUserId.bind).result.headOption
        } yield userDBIO

        for {
            authorizedUserOpt <- db.run(authorizedUserDBIO)
            result = authorizedUserOpt match {
                case Some(_) =>
                    Ok(Json.toJson(StatusResponse(StatusCode.OK.code, StatusCode.OK.message)))
                case None =>
                    Unauthorized(Json.toJson(StatusResponse(StatusCode.UNAUTHORIZED.code, StatusCode.UNAUTHORIZED.message)))
            }
        } yield result
    }

}

object LoginController {
    case class LoginForm(email : String, password : String )

    implicit val loginFormReads: Reads[LoginForm] = (
        (__ \ "email").read[String](email) and
        (__ \ "password").read[String](minLength[String](6))
    )(LoginForm)
}
