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
import play.api.libs.functional.syntax._

class ShoppingCenterController @Inject()(val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext)
    extends Controller with HasDatabaseConfigProvider[JdbcProfile] {

    import ShoppingCenterController._

    def list = Action.async { implicit rs =>
        val sessionUserId = rs.session.get(Constants.SESSION_TOKEN_USER_ID).getOrElse("0")
        db.run(Users.filter(t => t.userId === sessionUserId.toInt).result.headOption).flatMap{
            case Some(_) =>
                db.run(ShoppingCenters.sortBy(t => t.shoppingCenterId).result)
                        .map(shoppingCenters => Ok(Json.toJson(shoppingCenters)))
            case None =>
                Future.successful(Unauthorized(Json.toJson(StatusResponse(StatusCode.UNAUTHORIZED.code, StatusCode.UNAUTHORIZED.message))))
        }
    }

    def get(shoppingCenterId: Int) = Action.async {implicit rs =>
        val sessionUserId = rs.session.get(Constants.SESSION_TOKEN_USER_ID).getOrElse("0")
        val queryShoppingCenterById =
            ShoppingCenters.filter(t => t.shoppingCenterId === shoppingCenterId.bind).result.headOption

        db.run(Users.filter(t => t.userId === sessionUserId.toInt).result.headOption).flatMap {
            case Some(_) =>
                db.run(queryShoppingCenterById).map {
                    case Some(shoppingCenter) =>
                        Ok(Json.toJson(shoppingCenter))
                    case None =>
                        NotFound(Json.toJson(StatusResponse(StatusCode.RESOURCE_NOT_FOUND.code, StatusCode.RESOURCE_NOT_FOUND.message)))
                }
            case None =>
                Future.successful(Unauthorized(Json.toJson(StatusResponse(StatusCode.UNAUTHORIZED.code, StatusCode.UNAUTHORIZED.message))))
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