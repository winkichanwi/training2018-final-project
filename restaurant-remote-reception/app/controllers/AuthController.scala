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
import com.github.t3hnar.bcrypt._

class AuthController @Inject()(val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext)
    extends Controller with HasDatabaseConfigProvider[JdbcProfile]  {

    import AuthController._

    def login = Action.async(parse.json) { implicit rs =>
        rs.body.validate[LoginForm].map { form =>
            val loginEmail = form.email
            val loginPassword = form.password

            val queryUserInfoSecretDBIO = Users.join(UserSecret).on(_.userId === _.userId)
                .filter { case (t1, t2) => t1.email === loginEmail }
                .result.headOption

            val authenFailRes = StatusResponse(StatusCode.AUTHENTICATION_FAILURE.code, StatusCode.AUTHENTICATION_FAILURE.message)
            db.run(queryUserInfoSecretDBIO).map {
                case Some((user, userSecret)) =>
                    if (loginPassword.isBcrypted(userSecret.password)) {
                        Ok.withSession(Constants.SESSION_TOKEN_USER_ID -> user.userId.toString())
                    } else {
                        Unauthorized(Json.toJson(authenFailRes))
                    }
                case None =>
                    Unauthorized(Json.toJson(authenFailRes))
            }
        }.recoverTotal { e =>
            Future { BadRequest(Json.toJson(StatusResponse(StatusCode.UNSUPPORTED_FORMAT.code, StatusCode.UNSUPPORTED_FORMAT.message)))}
        }
    }

    def authenticate = Action.async { implicit rs =>
        val sessionUserId = rs.session.get(Constants.SESSION_TOKEN_USER_ID).getOrElse("0")
        val authorizedUserDBIO = Users.filter(t => t.userId === sessionUserId.toInt).result.headOption

        db.run(Users.filter(t => t.userId === sessionUserId.toInt).result.headOption).map {
            case Some(_) =>
                Ok
            case None =>
                Unauthorized(Json.toJson(StatusResponse(StatusCode.UNAUTHORIZED.code, StatusCode.UNAUTHORIZED.message)))
        }
    }

    def logout = Action.async { implicit rs =>
        Future { Ok(Json.toJson(StatusResponse(StatusCode.OK.code, StatusCode.OK.message))).withNewSession }
    }

}

object AuthController {
    case class LoginForm(email : String, password : String )

    implicit val loginFormReads: Reads[LoginForm] = (
        (__ \ "email").read[String](email) and
        (__ \ "password").read[String](minLength[String](8) keepAnd maxLength[String](20))
    )(LoginForm)
}
