package controllers

import com.google.inject.Inject
import models.User
import play.api.mvc.{Action, Controller}
import service.UserService
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.functional.syntax.unlift
import play.api.libs.json._
import play.api.libs.functional.syntax._

import scala.concurrent.Future

class UserJsonController @Inject()(userService: UserService, val messagesApi: MessagesApi) extends Controller with I18nSupport {

    import UserJsonController._
    def list = Action.async { implicit rs =>
        // IDの昇順にすべてのユーザ情報を取得
        userService.listAllUsers map { users =>
            // ユーザの一覧をJSONで返す
            Ok(Json.obj("users" -> users))
        }
    }

    def create() = Action.async(parse.json) { implicit rs =>
        rs.body.validate[UserForm].map { form =>
            // OKの場合はユーザを登録
            val user = User(0, form.fullName, form.email)
            userService.addUser(user).map(res =>
                Ok(Json.obj("result" -> "success"))
            )
        }.recoverTotal { e =>
            // NGの場合はバリデーションエラーを返す
            Future {
                BadRequest(Json.obj("result" ->"failure", "error" -> JsError.toJson(e)))
            }
        }
    }

    def update(id: Long) = TODO

    def delete(id: Long) = TODO

}

object UserJsonController {
    // UsersRowをJSONに変換するためのWritesを定義
    implicit val usersWrites: Writes[User] = (
        (__ \ "id"       ).write[Long]   and
        (__ \ "fullName"     ).write[String] and
        (__ \ "email").write[String]
    )(unlift(User.unapply))

    case class UserForm(id: Option[Long], fullName : String, email : String )

    // JSONをUserFormに変換するためのReadsを定義
    implicit val userFormReads: Reads[UserForm] = (
        (__ \ "id"       ).readNullable[Long] and
        (__ \ "fullName"     ).read[String]       and
        (__ \ "email").read[String]
    )(UserForm)
}