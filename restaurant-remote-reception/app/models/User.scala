package models

import play.api.data._
import play.api.data.Forms._

case class User(id : Long, fullName: String, email : String)

case class UserFormData(fullName : String, email : String )

object UserForm {
    val form = Form(
        mapping(
            "fullName" -> nonEmptyText,
            "email" -> email
        )(UserFormData.apply)(UserFormData.unapply)
    )
}