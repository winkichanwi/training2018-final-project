package controllers

import play.api.mvc._
import play.api.db.slick._
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile
import slick.driver.MySQLDriver.api._
import models.Tables._
import javax.inject.Inject
import models.{Constants, StatusCode, StatusResponse}

import scala.concurrent.ExecutionContext
import play.api.libs.json._
import play.api.libs.functional.syntax._

class ShoppingCenterController @Inject()(val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext)
    extends Controller with HasDatabaseConfigProvider[JdbcProfile] {

    import ShoppingCenterController._

    def list = Action.async { implicit rs =>
        val authorizedUserDBIO = for {
            sessionUserId <- rs.session.get(Constants.CACHE_TOKEN_USER_ID)
            userDBIO <- Users.filter(t => t.userId === sessionUserId.bind).result.headOption
        } yield userDBIO

        for {
            authorizedUserOpt <- db.run(authorizedUserDBIO)
            shoppingCenters <- db.run(ShoppingCenters.sortBy(t => t.shoppingCenterId).result)
            result = authorizedUserOpt match {
                case Some(_) =>
                    Ok(Json.toJson(shoppingCenters))
                case None =>
                    Unauthorized(Json.toJson(StatusResponse(StatusCode.UNAUTHORIZED.code, StatusCode.UNAUTHORIZED.message)))
            }
        } yield result
    }

    def get(shoppingCenterId: Int) = Action.async {implicit rs =>
        val authorizedUserDBIO = for {
            sessionUserId <- rs.session.get(Constants.CACHE_TOKEN_USER_ID)
            userDBIO <- Users.filter(t => t.userId === sessionUserId.bind).result.headOption
        } yield userDBIO

        val queryShoppingCenterById =
            ShoppingCenters.filter(t => t.shoppingCenterId === shoppingCenterId.bind).result.headOption

        for {
            authorizedUserOpt <- db.run(authorizedUserDBIO)
            shoppingCenterOpt <- db.run(queryShoppingCenterById)
            result = authorizedUserOpt match {
                case Some(_) => shoppingCenterOpt match {
                    case Some(shoppingCenter) =>
                        Ok(Json.toJson(shoppingCenter))
                    case None =>
                        NotFound(Json.toJson(StatusResponse(StatusCode.RESOURCE_NOT_FOUND.code, StatusCode.RESOURCE_NOT_FOUND.message)))
                }
                case None =>
                    Unauthorized(Json.toJson(StatusResponse(StatusCode.UNAUTHORIZED.code, StatusCode.UNAUTHORIZED.message)))
            }
        } yield result
    }
}

object ShoppingCenterController {
    implicit val shoppingCentersWrites: Writes[ShoppingCentersRow] = (
        (__ \ "id").write[Int]   and
        (__ \ "name").write[String] and
        (__ \ "branch").writeNullable[String]
    )(unlift(ShoppingCentersRow.unapply))
}