package controllers

import play.api.mvc._
import play.api.db.slick._
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import slick.driver.MySQLDriver.api._
import models.Tables._
import javax.inject.Inject
import scala.concurrent.Future

import play.api.libs.json._
import play.api.libs.functional.syntax._

class UserJsonController @Inject()(val dbConfigProvider: DatabaseConfigProvider)
    extends Controller with HasDatabaseConfigProvider[JdbcProfile] {
    // コンパニオンオブジェクトに定義したReads、Writesを参照するためにimport文を追加
    import UserJsonController._

    /**
      * 一覧表示
      */

    def list = Action.async { implicit rs =>
        // IDの昇順にすべてのユーザ情報を取得
        db.run(User.sortBy(t => t.userId).result).map { users =>
            // ユーザの一覧をJSONで返す
            Ok(Json.obj("users" -> users))
        }
    }

    /**
      * ユーザ登録
      */
    def create = Action.async(parse.json) { implicit rs =>
        rs.body.validate[UserForm].map { form =>
            // OKの場合はユーザを登録
            val user = UserRow(0, form.userFullname, form.userEmail)
            db.run(User += user).map { _ =>
                Ok(Json.obj("result" -> "success"))
            }
        }.recoverTotal { e =>
            // NGの場合はバリデーションエラーを返す
            Future {
                BadRequest(Json.obj("result" ->"failure", "error" -> JsError.toJson(e)))
            }
        }
    }
    //db.run(users.filter(_.id === id).result.headOption)

    /**
      * ユーザ更新
      */
    def update = Action.async(parse.json) { implicit rs =>
        rs.body.validate[UserForm].map { form =>
            // OKの場合はユーザ情報を更新
            val user = UserRow(form.userId.get, form.userFullname, form.userEmail)
            db.run(User.filter(t => t.userId === user.userId.bind).update(user)).map { _ =>
                Ok(Json.obj("result" -> "success"))
            }
        }.recoverTotal { e =>
            // NGの場合はバリデーションエラーを返す
            Future {
                BadRequest(Json.obj("result" ->"failure", "error" -> JsError.toJson(e)))
            }
        }
    }

    /**
      * ユーザ削除
      */
    def remove(userId: Int) = Action.async { implicit rs =>
        // ユーザを削除
        db.run(User.filter(t => t.userId === userId.bind).delete).map { _ =>
            Ok(Json.obj("result" -> "success"))
        }
    }
}

object UserJsonController {
    // UsersRowをJSONに変換するためのWritesを定義
    implicit val userWrites: Writes[UserRow] = (
        (__ \ "id"       ).write[Int]   and
        (__ \ "fullName"     ).write[String] and
        (__ \ "email").write[String]
    )(unlift(UserRow.unapply))

    case class UserForm(userId: Option[Int], userFullname : String, userEmail : String )

    // JSONをUserFormに変換するためのReadsを定義
    implicit val userFormReads: Reads[UserForm] = (
        (__ \ "id"       ).readNullable[Int] and
        (__ \ "full_name"     ).read[String] and
        (__ \ "email").read[String]
    )(UserForm)
}