package controllers

import play.api.mvc._
import play.api.db.slick._
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile
import slick.driver.MySQLDriver.api._
import models.Tables._
import javax.inject.Inject

import scala.concurrent.{ExecutionContext, Future}
import play.api.libs.json._
import play.api.libs.functional.syntax._

class LoginController @Inject()(val dbConfigProvider: DatabaseConfigProvider) (implicit ec: ExecutionContext)
    extends Controller with HasDatabaseConfigProvider[JdbcProfile]  {

    import LoginController._

    def authenticate = Action.async(parse.json) { implicit rs =>
        rs.body.validate[LoginForm].map { form =>
            var loginEmail = form.email
            var loginPassword = form.password

            val queryPassword = Users.join(UserSecret).on(_.userId === _.userId)
                .filter { case (t1, t2) =>
                    t1.email === loginEmail
                }.map { case (t1, t2) =>
                    (t1.userId, t1.userFullname, t2.password) <> (LoginInfo.tupled, LoginInfo.unapply)
                }.result.headOption

            db.run(queryPassword).map {
                case Some(loginInfo) =>
                    if (loginInfo.password == loginPassword) {
                        Ok(Json.obj("result" -> "success", "full_name" -> loginInfo.userFullname))
                            .withSession("connected" -> loginInfo.userId.toString())
                        // TODO set full name and id to cache?
                    } else {
                        BadRequest(Json.obj("result" ->"failure", "email" -> loginEmail, "login_password" -> loginPassword))
                    }
                case None => BadRequest(Json.obj("result" ->"failure", "email" -> loginEmail, "login_password" -> loginPassword))
            }
        }.recoverTotal { e =>
            // NGの場合はバリデーションエラーを返す
            Future {
                BadRequest(Json.obj("result" ->"failure", "error" -> JsError.toJson(e)))
            }
        }
    }

}

object LoginController {
    case class LoginInfo(userId: Int, userFullname: String, password: String)

    case class LoginForm(email : String, password : String )

    implicit val userFormReads: Reads[LoginForm] = (
        (__ \ "email").read[String] and
        (__ \ "password").read[String]
    )(LoginForm)
}
