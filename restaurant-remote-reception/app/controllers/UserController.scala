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

class UserController @Inject()(val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext)
    extends Controller with HasDatabaseConfigProvider[JdbcProfile] {
    // コンパニオンオブジェクトに定義したReads、Writesを参照するためにimport文を追加
    import UserController._

    /**
      * 一覧表示
      */

    def list = Action.async { implicit rs =>
        // IDの昇順にすべてのユーザ情報を取得
        db.run(Users.sortBy(t => t.userId).result).map { users =>
            // ユーザの一覧をJSONで返す
            Ok(Json.toJson(users))
        }
    }

    /**
      * ユーザ登録
      */
    def create = Action.async(parse.json) { implicit rs =>
        rs.body.validate[UserForm].map { form =>
            // OKの場合はユーザを登録
            val newUser = UsersRow(0, form.userFullname, form.email)
            def addUserDBIO = for {
                userId <- (Users returning Users.map(_.userId)) += newUser
                userAcc = UserSecretRow(userId, form.password)
                result <- UserSecret += userAcc
            } yield result

            db.run(addUserDBIO).map { _ =>
                Ok(Constants.SUCCESS_JSON)
            }
        }.recoverTotal { e =>
            Future { resForBadRequest(e) }
        }
    }

    def get(userId: Int) = Action.async { implicit rs =>
        def queryUserById = Users.filter(t => t.userId === userId.bind).result.headOption
        db.run(queryUserById).map{
            case Some(user) => Ok(Json.toJson(user))
            case None =>
                val errorResponse = ErrorResponse(Constants.FAILURE, "User is not found.")
                NotFound(Json.toJson(errorResponse))
        }
    }

    def getMe = Action.async { implicit rs =>
        val sessionUserId = rs.session.get(Constants.CACHE_TOKEN_USER_ID).getOrElse("0")
        def userDBIO = Users.filter(t => t.userId === sessionUserId.toInt).result.headOption
        def userFuture = db.run(userDBIO)
        userFuture.map {
            case Some(user) => Ok(Json.toJson(user))
            case None => NotFound(Json.toJson(ErrorResponse(Constants.FAILURE, "User not found")))
        }
    }

    /**
      * ユーザ更新
      */
    def update = TODO
//        Action.async(parse.json) { implicit rs =>
//        rs.body.validate[UserForm].map { form =>
//            // OKの場合はユーザ情報を更新
//            val user = UserRow(form.userId.get, form.userFullname, form.email)
//            db.run(User.filter(t => t.userId === user.userId.bind).update(user)).map { _ =>
//                Ok(Json.obj("result" -> "success"))
//            }
//        }.recoverTotal { e =>
//            // NGの場合はバリデーションエラーを返す
//            Future {
//                BadRequest(Json.obj("result" ->"failure", "error" -> JsError.toJson(e)))
//            }
//        }
//    }

    /**
      * ユーザ削除
      */
    def remove(userId: Int) = TODO
//        Action.async { implicit rs =>
//        // ユーザを削除
//        db.run(User.filter(t => t.userId === userId.bind).delete).map { _ =>
//            Ok(Json.obj("result" -> "success"))
//        }
//    }
}

object UserController {
    // UsersRowをJSONに変換するためのWritesを定義
    implicit val userWrites: Writes[UsersRow] = (
        (__ \ "id").write[Int]   and
        (__ \ "full_name").write[String] and
        (__ \ "email").write[String]
    )(unlift(UsersRow.unapply))

    case class UserForm(userId: Option[Int], userFullname : String, email : String, password : String )

    // JSONをUserFormに変換するためのReadsを定義
    implicit val userFormReads: Reads[UserForm] = (
        (__ \ "id").readNullable[Int] and
        (__ \ "full_name").read[String] and
        (__ \ "email").read[String](email) and
        (__ \ "password").read[String](minLength[String](6))
    )(UserForm)
}