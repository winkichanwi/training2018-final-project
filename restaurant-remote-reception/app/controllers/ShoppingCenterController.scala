package controllers

import play.api.mvc._
import play.api.db.slick._
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile
import slick.driver.MySQLDriver.api._
import models.Tables._
import javax.inject.Inject
import models.ErrorResponse._
import models.{Constants, ErrorResponse}

import scala.concurrent.{ExecutionContext, Future}
import play.api.libs.json._
import play.api.libs.functional.syntax._

class ShoppingCenterController @Inject()(val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext)
    extends Controller with HasDatabaseConfigProvider[JdbcProfile] {

    import ShoppingCenterController._

    def list = Action.async { implicit rs =>
        val sessionUserId = rs.session.get(Constants.CACHE_TOKEN_USER_ID).getOrElse("0")

        db.run(Users.filter(t => t.userId === sessionUserId.toInt).result.headOption).flatMap{
            case Some(_) =>
                db.run(ShoppingCenters.sortBy(t => t.shoppingCenterId).result)
                        .map(shoppingCenters => Ok(Json.toJson(shoppingCenters)))
            case None =>
                Future.successful(Unauthorized(Json.toJson(ErrorResponse(Constants.FAILURE, "Not yet logged in!"))))
        }
    }

    def get(shoppingCenterId: Int) = Action.async {implicit rs =>
        val queryShoppingCenterById =
            ShoppingCenters.filter(t => t.shoppingCenterId === shoppingCenterId.bind).result.headOption
        db.run(queryShoppingCenterById).map {
            case Some(shoppingCenter) => Ok(Json.toJson(shoppingCenter))
            case None => {
                val errorResponse = ErrorResponse(Constants.FAILURE, "Shopping center (id: " + shoppingCenterId + ") is not found.")
                NotFound(Json.toJson(errorResponse))
            }
        }
    }
}

object ShoppingCenterController {
    implicit val shoppingCentersWrites: Writes[ShoppingCentersRow] = (
        (__ \ "id").write[Int]   and
        (__ \ "name").write[String] and
        (__ \ "branch").writeNullable[String]
    )(unlift(ShoppingCentersRow.unapply))
}