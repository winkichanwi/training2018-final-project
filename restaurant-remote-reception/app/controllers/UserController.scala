package controllers

import com.google.inject.Inject
import models.{User, UserForm}
import play.api.mvc.{Action, Controller}
import service.UserService
import scala.concurrent.Future
import play.api.i18n.{MessagesApi, Messages, I18nSupport}
import play.api.libs.concurrent.Execution.Implicits._

class UserController @Inject()
(userService: UserService,
 val messagesApi: MessagesApi) extends Controller with I18nSupport {

    def home = Action.async { implicit  request =>
        userService.listAllUsers map { users =>
            Ok(views.html.user(UserForm.form,users))
        }
    }

    def addUser() = Action.async { implicit request =>
        UserForm.form.bindFromRequest.fold(
            // if any error in submitted data
            errorForm => Future.successful(Ok(views.html.user(errorForm,Seq.empty[User]))),
            data => {
                val newUser = User(0,data.fullName,data.email)
                userService.addUser(newUser).map(res =>
                    Redirect(routes.UserController.home()).flashing(Messages("flash.success") -> res)
                )
            })
    }

    def deleteUser(id : Long) = Action.async { implicit request =>
        userService.deleteUser(id) map { res =>
            Redirect(routes.UserController.home())
        }
    }

}