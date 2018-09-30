package controllers

import play.api.mvc._
import play.api.db.slick._
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile
import slick.driver.MySQLDriver.api._
import models.Tables._
import javax.inject.Inject
import models.StatusCode

import scala.concurrent.ExecutionContext
import play.api.libs.json._
import play.api.libs.functional.syntax._
import repositories.{ShoppingCenterRepository, UserRepository}
import security.SecureComponent

/**
  * Controller for shopping center
  */
class ShoppingCenterController @Inject()(
    val userRepo: UserRepository,
    val shoppingCenterRepo: ShoppingCenterRepository,
    val dbConfigProvider: DatabaseConfigProvider)(
    implicit ec: ExecutionContext)
    extends Controller with HasDatabaseConfigProvider[JdbcProfile] with SecureComponent {

    import ShoppingCenterController._

    /**
      * [Authentication required]
      * Getting list of shopping centers
      * @return Future[Result] Body containing list of shopping centers
      */
    def list = SecureAction.async { implicit rs =>
        db.run(shoppingCenterRepo.list)
            .map{
                case shoppingCenters if shoppingCenters.nonEmpty =>
                    Ok(Json.toJson(shoppingCenters))
                case shoppingCenters if shoppingCenters.isEmpty =>
                    NotFound(StatusCode.RESOURCE_NOT_FOUND.genJsonResponse)
            }
    }

    /**
      * [Authentication required]
      * Getting shopping center information by id
      * @param shoppingCenterId ID of a shopping center
      * @return Future[Result] Body containing information of queried shopping center
      */
    def get(shoppingCenterId: Int) = SecureAction.async {implicit rs =>
        db.run(shoppingCenterRepo.findById(shoppingCenterId)).map {
            case Some(shoppingCenter) =>
                Ok(Json.toJson(shoppingCenter))
            case None =>
                NotFound(StatusCode.RESOURCE_NOT_FOUND.genJsonResponse)
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