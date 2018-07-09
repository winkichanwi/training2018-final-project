package controllers

import javax.inject.Inject
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.json.Writes
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.mvc.{Action, Controller}
import slick.driver.JdbcProfile
import slick.driver.MySQLDriver.api._
import models.Tables._
import models.TicketStatus

import scala.concurrent.ExecutionContext

class TicketController @Inject()(val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext)
    extends Controller with HasDatabaseConfigProvider[JdbcProfile]  {

    import TicketController._

    private val activeTicketStatusSeq: Seq[String] = TicketStatus.values.filter(_.isActive).map(value => value.toString).toSeq

    def countTickets(restaurantId: Int) = Action.async { implicit rs =>
        Tickets.filter(t =>
            (t.restaurantId === restaurantId.bind) &&
            (t.ticketStatus inSet activeTicketStatusSeq))
            .groupBy(_.ticketType)
            .map { case (ticketType, t) =>
                ticketType, t.length
            }

        db.run().map {
            Ok(Json.toJson())
        }
    }
}

object TicketController {
    case class RestaurantTicketCounts(ticketType: String, ticketCount: Int)

    implicit val restaurantTicketCountWrites: Writes[RestaurantTicketCounts] = (
        (__ \ "ticket_type").write[String] and
        (__ \ "ticket_count").write[String]
    )(unlift(RestaurantTicketCounts.unapply))
}