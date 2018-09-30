package repositories

import javax.inject.Inject
import models.Tables.ShoppingCenters
import play.api.db.slick._
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile
import slick.driver.MySQLDriver.api._

import scala.concurrent.ExecutionContext

class ShoppingCenterRepository  @Inject()
    (val dbConfigProvider: DatabaseConfigProvider)
    (implicit ec: ExecutionContext)
    extends HasDatabaseConfigProvider[JdbcProfile] {

    def list =
        ShoppingCenters.sortBy(t => t.shoppingCenterId).result

    def findById(shoppingCenterId: Int) =
        ShoppingCenters.filter(t => t.shoppingCenterId === shoppingCenterId.bind).result.headOption

}
