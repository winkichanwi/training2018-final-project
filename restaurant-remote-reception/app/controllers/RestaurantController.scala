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

import scala.concurrent.{ExecutionContext, Future}

/**
  * Controller for restaurants
  */
class RestaurantController @Inject()(val dbConfigProvider: DatabaseConfigProvider) (implicit ec: ExecutionContext)
    extends Controller with HasDatabaseConfigProvider[JdbcProfile] {

    import RestaurantController._

    /**
      * [Authentication required]
      * Get list of restaurants of specified shopping center
      * @param shoppingCenterId ID of specified shopping center
      * @return Future[Result] Body containing list of restaurants
      */
    def list(shoppingCenterId: Int) = Action.async { implicit rs =>
        val sessionUserId = rs.session.get(Constants.SESSION_TOKEN_USER_ID).getOrElse("0")
        val queryRestaurantsByShoppingCenterId =
            Restaurants.filter(t => t.shoppingCenterId === shoppingCenterId.bind).result
        db.run(Users.filter(t => t.userId === sessionUserId.toInt).result.headOption).flatMap {
            case Some(_) =>
                db.run(queryRestaurantsByShoppingCenterId)
                    .map{
                        case restaurants if restaurants.nonEmpty =>
                            Ok(Json.toJson(restaurants))
                        case restaurants if restaurants.isEmpty =>
                            NotFound(StatusCode.RESOURCE_NOT_FOUND.genJsonResponse)
                    }
            case None =>
                Future.successful(Unauthorized(Json.toJson(StatusCode.UNAUTHORIZED.genJsonResponse)))
        }
    }

    /**
      * [Authentication required]
      * Get information of specified restaurant
      * @param restaurantId ID of specified restaurant
      * @return Future[Result] Body containing information of restaurant
      */
    def get(restaurantId: Int) = Action.async { implicit rs =>
        val sessionUserId = rs.session.get(Constants.SESSION_TOKEN_USER_ID).getOrElse("0")
        val queryRestaurantById =
            Restaurants.filter(t => t.restaurantId === restaurantId.bind).result.headOption
        db.run(Users.filter(t => t.userId === sessionUserId.toInt).result.headOption).flatMap {
            case Some(_) =>
                db.run(queryRestaurantById).map {
                    case Some(restaurant) =>
                        Ok(Json.toJson(restaurant))
                    case None =>
                        NotFound(StatusCode.RESOURCE_NOT_FOUND.genJsonResponse)
                }
            case None =>
                Future.successful(Unauthorized(StatusCode.UNAUTHORIZED.genJsonResponse))
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