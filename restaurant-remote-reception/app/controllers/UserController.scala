package controllers

import controllers.UserController._
import javax.inject.Inject
import models.StatusCode
import models.Tables._
import play.api.db.slick.{DatabaseConfigProvider, _}

import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._
import play.api.mvc._
import repositories.UserRepository

import security.SecureComponent
import slick.driver.JdbcProfile
import scala.concurrent.{ExecutionContext, Future}

/**
  * Controller for user related actions
  */
class UserController @Inject()(
     val userRepo: UserRepository,
     val dbConfigProvider: DatabaseConfigProvider)(
     implicit ec: ExecutionContext)
    extends Controller with HasDatabaseConfigProvider[JdbcProfile] with SecureComponent {

    /**
      * Listing existing users
      * @return Future[Result] Body containing list of users
      */
    def list = SecureAction.async { implicit rs =>
        // IDの昇順にすべてのユーザ情報を取得
        db.run(userRepo.list).map { users =>
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
            db.run(userRepo.create(form.userFullname, form.email, form.password)).map(_ => Ok)
        }.recoverTotal { e =>
            Future .successful(BadRequest(StatusCode.UNSUPPORTED_FORMAT.genJsonResponse))
        }
    }

    /**
      * [Authentication required]
      * User information: get logged in user information
      * @return Future[Result] Body containing logged in user information
      */
    def getCurrentUser = Action.async { implicit rs =>
        db.run(userRepo.findById).map {
            case Some(user) => Ok(Json.toJson(user))
            case None => Unauthorized(StatusCode.UNAUTHORIZED.genJsonResponse)
        }
    }

    /**
      * [Authentication required]
      * User information update: update user information
      * @return Future[Result] Result of updating user information
      */
    def update = SecureAction.async(parse.json) { implicit rs =>
        rs.body.validate[UsersRow].map { form => {
                if (form.userId != request2SessionUserId.value)
                    // unauthorized for updating information of another user
                    Future.successful(Unauthorized(StatusCode.UNAUTHORIZED.genJsonResponse))
                else {
                    db.run(userRepo.update(form.userId, form.userFullname, form.email)).map { _ => Ok }
                }
            }
        }.recoverTotal { e =>
            Future.successful(BadRequest(StatusCode.UNSUPPORTED_FORMAT.genJsonResponse))
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