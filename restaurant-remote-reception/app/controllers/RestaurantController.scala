package controllers

import javax.inject.Inject
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.functional.syntax.unlift
import play.api.libs.json.{Writes, __}
import play.api.mvc.Controller

import models.StatusCode
import models.Tables._
import play.api.libs.functional.syntax._
import play.api.libs.json._
import repositories.UserRepository

import security.SecureComponent
import slick.driver.JdbcProfile
import slick.driver.MySQLDriver.api._
import scala.concurrent.ExecutionContext

/**
  * Controller for restaurants
  */
class RestaurantController @Inject()(
    val userRepo: UserRepository,
    val dbConfigProvider: DatabaseConfigProvider)(
    implicit ec: ExecutionContext)
    extends Controller with HasDatabaseConfigProvider[JdbcProfile] with SecureComponent {

    import RestaurantController._

    /**
      * [Authentication required]
      * Get list of restaurants of specified shopping center
      * @param shoppingCenterId ID of specified shopping center
      * @return Future[Result] Body containing list of restaurants
      */
    def list(shoppingCenterId: Int) = SecureAction.async { implicit rs =>
        val queryRestaurantsByShoppingCenterId =
            Restaurants.filter(t => t.shoppingCenterId === shoppingCenterId.bind).result

        db.run(queryRestaurantsByShoppingCenterId)
            .map{
                case restaurants if restaurants.nonEmpty =>
                    Ok(Json.toJson(restaurants))
                case restaurants if restaurants.isEmpty =>
                    NotFound(StatusCode.RESOURCE_NOT_FOUND.genJsonResponse)
            }
    }

    /**
      * [Authentication required]
      * Get information of specified restaurant
      * @param restaurantId ID of specified restaurant
      * @return Future[Result] Body containing information of restaurant
      */
    def get(restaurantId: Int) = SecureAction.async { implicit rs =>
        val queryRestaurantById =
            Restaurants.filter(t => t.restaurantId === restaurantId.bind).result.headOption
        db.run(queryRestaurantById).map {
            case Some(restaurant) =>
                Ok(Json.toJson(restaurant))
            case None =>
                NotFound(StatusCode.RESOURCE_NOT_FOUND.genJsonResponse)
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