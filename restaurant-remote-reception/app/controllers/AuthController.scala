package controllers

import play.api.mvc._
import play.api.db.slick._
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile
import slick.driver.MySQLDriver.api._
import models.Tables._
import javax.inject.Inject
import models.{Constants, StatusCode}

import scala.concurrent.{ExecutionContext, Future}
import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._
import com.github.t3hnar.bcrypt._
import repositories.UserRepository
import security.SecureComponent

/**
  * Controller for user authentication actions
  */
class AuthController @Inject()(
      val userRepo: UserRepository,
      val dbConfigProvider: DatabaseConfigProvider)(
      implicit ec: ExecutionContext)
    extends Controller with HasDatabaseConfigProvider[JdbcProfile] with SecureComponent {

    /**
      * User login: authenticate user login information by comparing the password
      * @return Future[Result] Result for authentication result and set session if user is successfully logged in
      */
    def login = Action.async(parse.json) { implicit rs =>
        rs.body.validate[UserLoginForm].map { form =>
            db.run(userRepo.fetchInfoAndSecretByEmail(form.email)).map {
                case Some((user, userSecret)) =>
                    if (form.password.isBcrypted(userSecret.password)) {
                        Ok.withSession(Constants.SESSION_TOKEN_USER_ID -> user.userId.toString())
                    } else {
                        Unauthorized(StatusCode.AUTHENTICATION_FAILURE.genJsonResponse)
                    }
                case None =>
                    Unauthorized(StatusCode.AUTHENTICATION_FAILURE.genJsonResponse)
            }
        }.recoverTotal { e =>
            Future { BadRequest(StatusCode.UNSUPPORTED_FORMAT.genJsonResponse)}
        }
    }

    /**
      * User authentication: determine whether a user has been authenticated or not
      * @return Future[Result] Result for authentication result
      */
    def authenticate = SecureAction.async { implicit rs => Future.successful(Ok) }

    /**
      * User logout: renew user login session to logout the user
      * @return Future[Result] Result representing logout succeeded
      */
    def logout = Action.async { implicit rs => Future.successful(Ok.withNewSession) }
}

/**
  * Template for reading user login information
  * @param email user email for logging in and retrieving information from user table
  * @param password user password for authentication
  */
case class UserLoginForm(email : String, password : String )

object UserLoginForm {
    implicit val loginFormReads: Reads[UserLoginForm] = (
        (__ \ "email").read[String](email) and
        (__ \ "password").read[String](minLength[String](8) keepAnd maxLength[String](20))
    )(UserLoginForm.apply _)
}
