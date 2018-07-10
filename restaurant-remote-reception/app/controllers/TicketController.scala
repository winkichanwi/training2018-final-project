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

import scala.concurrent.{ExecutionContext, Future}

class TicketController @Inject()(val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext)
    extends Controller with HasDatabaseConfigProvider[JdbcProfile]  {

    import TicketController._

    private val activeTicketStatusSeq: Seq[String] = TicketStatus.values.filter(_.isActive).map(value => value.toString).toSeq

    def countTickets(restaurantId: Int) = Action.async { implicit rs =>
        def groupTicketTypes = Tickets.filter(t =>
            (t.restaurantId === restaurantId.bind) && (t.ticketStatus inSet activeTicketStatusSeq))
            .groupBy(_.ticketType)
        def queryTicketCounts = for {
         (ticketType, ticketRows) <- groupTicketTypes
        } yield (ticketType, ticketRows.length)

        for {
            ticketCountRows: Seq[(String, Int)] <- db.run(queryTicketCounts.result)
            counts = ticketCountRows.map { row =>
                RestaurantTicketCounts(row._1, row._2)
            }
        } yield Ok(Json.toJson(counts))
    }
}

object TicketController {
    case class RestaurantTicketCounts(ticketType: String, ticketCount: Int)

    implicit val restaurantTicketCountWrites: Writes[RestaurantTicketCounts] = (
        (__ \ "ticket_type").write[String] and
        (__ \ "ticket_count").write[Int]
    )(unlift(RestaurantTicketCounts.unapply))
}