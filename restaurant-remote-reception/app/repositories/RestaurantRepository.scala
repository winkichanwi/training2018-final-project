package repositories

import javax.inject.Inject
import models.Tables.Restaurants
import play.api.db.slick._
import slick.driver.MySQLDriver.api._
import slick.driver.JdbcProfile

import scala.concurrent.ExecutionContext

class RestaurantRepository @Inject()
    (val dbConfigProvider: DatabaseConfigProvider)
    (implicit ec: ExecutionContext)
    extends HasDatabaseConfigProvider[JdbcProfile]  {

    def findById(restaurantId: Int) =
        Restaurants.filter(t => t.restaurantId === restaurantId.bind).result.headOption

    def listByShoppingCenterId(shoppingCenterId: Int) =
        Restaurants.filter(t => t.shoppingCenterId === shoppingCenterId.bind).result

}
