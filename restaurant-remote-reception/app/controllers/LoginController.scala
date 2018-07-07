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

class LoginController @Inject()(val dbConfigProvider: DatabaseConfigProvider) (implicit ec: ExecutionContext)
    extends Controller with HasDatabaseConfigProvider[JdbcProfile]  {

    import LoginController._

    def authenticate = Action.async(parse.json) { implicit rs =>
        rs.body.validate[LoginForm].map { form =>
            var loginEmail = form.email
            var loginPassword = form.password

            val queryPasswordByEmail = Users.join(UserSecret).on(_.userId === _.userId)
                .filter { case (t1, t2) =>
                    t1.email === loginEmail
                }.map { case (t1, t2) =>
                    (t1.userId, t1.userFullname, t2.password) <> (LoginInfo.tupled, LoginInfo.unapply)
                }.result.headOption

            db.run(queryPasswordByEmail).map {
                case Some(loginInfo) =>
                    if (loginInfo.password == loginPassword) {
                        Ok(Json.obj("result" -> Constants.success, "full_name" -> loginInfo.userFullname))
                            .withSession("connected" -> loginInfo.userId.toString())
                        // TODO set full name and id to cache?
                    } else {
                        val pwdIncorrectRes = ErrorResponse(Constants.failure, "Password incorrect. ")
                        BadRequest(Json.toJson(pwdIncorrectRes))
                    }
                case None =>
                    val emailIncorrectRes = ErrorResponse(Constants.failure, "Email (" + loginEmail + ") incorrect. ")
                    BadRequest(Json.toJson(emailIncorrectRes))
            }
        }.recoverTotal { e =>
            Future { resForBadRequest(e) }
        }
    }

}

object LoginController {
    case class LoginInfo(userId: Int, userFullname: String, password: String)

    case class LoginForm(email : String, password : String )

    implicit val userFormReads: Reads[LoginForm] = (
        (__ \ "email").read[String](email) and
        (__ \ "password").read[String](minLength[String](6))
    )(LoginForm)
}
