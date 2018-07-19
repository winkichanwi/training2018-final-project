package controllers

import javax.inject.Inject
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.functional.syntax.unlift
import play.api.libs.json.{Writes, __}
import play.api.mvc.{Action, Controller}
import slick.driver.JdbcProfile
import slick.driver.MySQLDriver.api._
import models.Tables._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import models.{Constants, StatusCode, StatusResponse}

import scala.concurrent.ExecutionContext

class RestaurantController @Inject()(val dbConfigProvider: DatabaseConfigProvider) (implicit ec: ExecutionContext)
    extends Controller with HasDatabaseConfigProvider[JdbcProfile] {

    import RestaurantController._

    def list(shoppingCenterId: Int) = Action.async { implicit rs =>
        val sessionUserIdOpt = rs.session.get(Constants.CACHE_TOKEN_USER_ID)
        val queryRestaurantsByShoppingCenterId =
            Restaurants.filter(t => t.shoppingCenterId === shoppingCenterId.bind).result
        for {
            restaurants <- db.run(queryRestaurantsByShoppingCenterId)
            result = sessionUserIdOpt match {
                case Some(_) => Ok(Json.toJson(restaurants))
                case None => Unauthorized(Json.toJson(StatusResponse(StatusCode.UNAUTHORIZED.code, StatusCode.UNAUTHORIZED.message)))
            }
        } yield result
    }

    def get(restaurantId: Int) = Action.async { implicit rs =>
        val queryRestaurantById =
            Restaurants.filter(t => t.restaurantId === restaurantId.bind).result.headOption
        db.run(queryRestaurantById).map {
            case Some(restaurant) =>
                Ok(Json.toJson(restaurant))
            case None =>
                NotFound(Json.toJson(StatusResponse(StatusCode.RESOURCE_NOT_FOUND.code, StatusCode.RESOURCE_NOT_FOUND.message)))
        }
    }

}

object RestaurantController {
    implicit val restaurantsWrites: Writes[RestaurantsRow] = (
        (__ \ "id").write[Int] and
        (__ \ "name").write[String] and
        (__ \ "shopping_center_id").write[Int] and
        (__ \ "status").write[String] and
        (__ \ "floor").write[String] and
        (__ \ "seat_no").write[Int] and
        (__ \ "cuisine").write[String] and
        (__ \ "phone_no").write[String] and
        (__ \ "opening_hour").write[String] and
        (__ \ "image_url").writeNullable[String]
    )(unlift(RestaurantsRow.unapply))
}