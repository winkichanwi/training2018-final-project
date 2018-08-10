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
import controllers.Utils._

/**
  * Controller for user related actions
  */
class UserController @Inject()(val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext)
    extends Controller with HasDatabaseConfigProvider[JdbcProfile] {

    /**
      * Listing existing users
      * @return Future[Result] Body containing list of users
      */
    def list = Action.async { implicit rs =>
        // IDの昇順にすべてのユーザ情報を取得
        db.run(Users.sortBy(t => t.userId).result).map { users =>
            // ユーザの一覧をJSONで返す
            Ok(Json.toJson(users))
        }
    }

    /**
      * User signing up: create user record in both user table and user secret table
      * @return Future[Result] Result for user record creation result
      */
    def create = Action.async(parse.json) { implicit rs =>
        rs.body.validate[UserSignUpForm].map { form =>
            val addUser = for {
                userId <- (Users returning Users.map(_.userId)) += UsersRow(0, form.userFullname, form.email)
                result <- UserSecret += UserSecretRow(userId, form.password.bcrypt)
            } yield result

            db.run(addUser).map(_ => Ok)
        }.recoverTotal { e =>
            Future { BadRequest(Json.toJson(StatusResponse(StatusCode.UNSUPPORTED_FORMAT.code, StatusCode.UNSUPPORTED_FORMAT.message)))}
        }
    }

    /**
      * [Authentication required]
      * User information: get logged in user information
      * @return Future[Result] Body containing logged in user information
      */
    def getCurrentUser = Action.async { implicit rs =>
        db.run(getSessionUser(rs)).map {
            case Some(user) =>
                Ok(Json.toJson(user))
            case None =>
            Unauthorized(Json.toJson(StatusResponse(StatusCode.UNAUTHORIZED.code, StatusCode.UNAUTHORIZED.message)))
        }
    }

    /**
      * [Authentication required]
      * User information update: update user information
      * @return Future[Result] Result of updating user information
      */
    def update = Action.async(parse.json) { implicit rs =>
        val sessionUserId = rs.session.get(Constants.SESSION_TOKEN_USER_ID).getOrElse("0")
        db.run(Users.filter(t => t.userId === sessionUserId.toInt).result.headOption).flatMap {
            case Some(_) =>
                rs.body.validate[UsersRow].map { form => {
                        if (form.userId != sessionUserId.toInt)
                            Future.successful(Unauthorized(Json.toJson(StatusResponse(StatusCode.UNAUTHORIZED.code, StatusCode.UNAUTHORIZED.message))))
                        else {
                            val user = UsersRow(form.userId, form.userFullname, form.email)
                            db.run(Users.filter(t => t.userId === user.userId.bind).update(user)).map { _ =>
                                Ok
                            }
                        }
                    }
                }.recoverTotal { e =>
                    Future.successful(BadRequest(Json.toJson(StatusResponse(StatusCode.UNSUPPORTED_FORMAT.code, StatusCode.UNSUPPORTED_FORMAT.message))))
                }
            case None =>
                Future.successful(Unauthorized(Json.toJson(StatusResponse(StatusCode.UNAUTHORIZED.code, StatusCode.UNAUTHORIZED.message))))
        }
    }
}

object UserController {
    implicit val userWrites: Writes[UsersRow] = (
        (__ \ "id").write[Int]   and
        (__ \ "full_name").write[String] and
        (__ \ "email").write[String]
    )(unlift(UsersRow.unapply))

    implicit val userReads: Reads[UsersRow] = (
        (__ \ "id").read[Int]   and
        (__ \ "full_name").read[String] and
        (__ \ "email").read[String]
    )(UsersRow)
}

/**
  * Template for user signing up
  * @param userId Option[Int] User id for identifying user, autoincrement
  * @param userFullname String User's full name
  * @param email String User email for loggin in
  * @param password String User password for authentication use, hashed in database
  */
case class UserSignUpForm(userId: Option[Int], userFullname : String, email : String, password : String )

object UserSignUpForm {
    implicit val userFormReads: Reads[UserSignUpForm] = (
        (__ \ "id").readNullable[Int] and
        (__ \ "full_name").read[String] and
        (__ \ "email").read[String](email) and
        (__ \ "password").read[String](minLength[String](8) keepAnd maxLength[String](20))
    )(UserSignUpForm.apply _)
}