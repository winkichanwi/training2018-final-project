package controllers

import play.api.mvc._
import play.api.db.slick._
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile
import slick.driver.MySQLDriver.api._
import models.Tables._
import javax.inject.Inject
import models.ErrorResponse._
import models.Utils._
import models.{Constants, ErrorResponse}

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

            def queryUserInfoSecretDBIO = Users.join(UserSecret).on(_.userId === _.userId)
                .filter { case (t1, t2) =>
                    t1.email === loginEmail
                }.result.headOption

            db.run(queryUserInfoSecretDBIO).map {
                case Some((user, userSecret)) =>
                    if (userSecret.password == loginPassword) {
                        Ok(Json.obj("result" -> Constants.SUCCESS))
                            .withSession(Constants.CACHE_TOKEN_USER_ID -> user.userId.toString())
                        // TODO set full name and id to cache?
                    } else {
                        val pwdIncorrectRes = ErrorResponse(Constants.FAILURE, "Password incorrect. ")
                        BadRequest(Json.toJson(pwdIncorrectRes))
                    }
                case None =>
                    val emailIncorrectRes = ErrorResponse(Constants.FAILURE, "Email address (" + loginEmail + ") incorrect. ")
                    BadRequest(Json.toJson(emailIncorrectRes))
            }
        }.recoverTotal { e =>
            Future { resForBadRequest(e) }
        }
    }

}

object LoginController {
    case class LoginForm(email : String, password : String )

    implicit val userFormReads: Reads[LoginForm] = (
        (__ \ "email").read[String](email) and
        (__ \ "password").read[String](minLength[String](6))
    )(LoginForm)
}
