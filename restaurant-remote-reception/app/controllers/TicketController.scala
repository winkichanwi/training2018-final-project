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

    def countTickets(restaurantId: Int) = Action.async { implicit rs =>
        val activeTicketStatusSeq: Seq[String] = Seq(TicketStatus.ACTIVE, TicketStatus.CALLED).map(_.toString)
        val groupTicketTypes = Tickets.filter(t =>
            (t.restaurantId === restaurantId.bind) && (t.ticketStatus inSet activeTicketStatusSeq))
            .groupBy(_.ticketType)
            .map { case (ticketType, rows) => (ticketType, rows.length) }
            .result

        db.run(groupTicketTypes)
            .map(_.map(row => RestaurantTicketCounts(row._1, row._2)))
            .map(counts => Ok(Json.toJson(counts)))
    }

    def getLastTicket(restaurantId: Int) = Action.async { implicit rs =>
        def queryTicketLastCalled = Tickets.filter(t =>
            (t.restaurantId === restaurantId.bind) && (t.ticketStatus === TicketStatus.CALLED.status))
            .sortBy(_.ticketNo.desc)
            .groupBy(_.ticketType)
            .map { case (ticketType, ticketRows) =>
                (ticketType, ticketRows.map(_.ticketNo).max)
            }

        def queryTicketLastTaken = Tickets.filter(t =>
            (t.restaurantId === restaurantId.bind) && (t.ticketStatus =!= TicketStatus.ARCHIVED.status))
            .sortBy(_.ticketNo.desc)
            .groupBy(_.ticketType)
            .map { case (ticketType, ticketRows) =>
                (ticketType, ticketRows.map(_.ticketNo).max)
            }

        def joinLastCalledLastTaken =
            queryTicketLastCalled.joinFull(queryTicketLastTaken).on(_._1 === _._1)
            .map { case (t1, t2) =>
                (t1.flatMap(_._1.?), t1.flatMap(_._2), t2.flatMap(_._1.?), t2.flatMap(_._2))
            }

        for {
            ticketLastNoRows: Seq[(Option[String], Option[Int], Option[String], Option[Int])] <- db.run(joinLastCalledLastTaken.result)
            ticketLastNo = ticketLastNoRows.map { row =>
                val ticketType = row._1.getOrElse(row._3.get)
                val lastCalled = row._2.getOrElse(0)
                val lastTaken = row._4.getOrElse(0)
                RestaurantTicketLastNo(ticketType, lastCalled, lastTaken)
            }
        } yield Ok(Json.toJson(ticketLastNo))
    }
}

case class RestaurantTicketCounts(ticketType: String, ticketCount: Int)

object RestaurantTicketCounts {
    implicit val restaurantTicketCountWrites: Writes[RestaurantTicketCounts] = (
        (__ \ "ticket_type").write[String] and
        (__ \ "ticket_count").write[Int]
    )(unlift(RestaurantTicketCounts.unapply))
}


case class RestaurantTicketLastNo(ticketType: String, lastCalled: Int, lastTaken: Int)

object RestaurantTicketLastNo {
    implicit val restaurantTicketLastNoWrites: Writes[RestaurantTicketLastNo] = (
        (__ \ "ticket_type").write[String] and
            (__ \ "last_called").write[Int] and
            (__ \ "last_taken").write[Int]
        ) (unlift(RestaurantTicketLastNo.unapply))
}