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

import scala.concurrent.ExecutionContext

class ShoppingCenterController @Inject()(val dbConfigProvider: DatabaseConfigProvider) (implicit ec: ExecutionContext)
    extends Controller with HasDatabaseConfigProvider[JdbcProfile] {

    import ShoppingCenterController._

    def list = Action.async(parse.json) { implicit rs =>
        db.run(ShoppingCenters.sortBy(t => t.shoppingCenterId).result).map { shoppingCenters =>
            Ok(Json.obj("shopping_centers" -> shoppingCenters))
        }
    }
}

object ShoppingCenterController {
    implicit val shoppingCentersWrites: Writes[ShoppingCentersRow] = (
        (__ \ "id").write[Int]   and
        (__ \ "name").write[String] and
        (__ \ "branch").write[Option[String]]
    )(unlift(ShoppingCentersRow.unapply))
}