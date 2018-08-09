package controllers

import java.sql.Timestamp
import java.time.LocalDateTime

import javax.inject.Inject
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.json.Writes
import play.api.libs.json.Reads._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.mvc.{Action, Controller}
import slick.driver.JdbcProfile
import slick.driver.MySQLDriver.api._
import models.Tables._
import models._

import scala.concurrent.{ExecutionContext, Future}

class TicketController @Inject()(val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext)
    extends Controller with HasDatabaseConfigProvider[JdbcProfile]  {

    def countTickets(restaurantId: Int) = Action.async { implicit rs =>
        val sessionUserId = rs.session.get(Constants.SESSION_TOKEN_USER_ID).getOrElse("0")

        val activeTicketStatusSeq: Seq[String] = Seq(TicketStatus.ACTIVE, TicketStatus.CALLED).map(_.toString)
        val groupTicketTypes = Tickets.filter(t =>
            (t.restaurantId === restaurantId.bind) && (t.ticketStatus inSet activeTicketStatusSeq))
            .groupBy(_.ticketType)
            .map { case (ticketType, rows) => (ticketType, rows.length) }
            .result

        db.run(Users.filter(t => t.userId === sessionUserId.toInt).result.headOption).flatMap {
            case Some(_) =>
                db.run(groupTicketTypes)
                    .map(_.map(row => RestaurantTicketCounts(row._1, row._2)))
                    .map(counts => Ok(Json.toJson(counts)))
            case None =>
                Future.successful(Unauthorized(Json.toJson(StatusResponse(StatusCode.UNAUTHORIZED.code, StatusCode.UNAUTHORIZED.message))))
        }
    }

    def getLastTicket(restaurantId: Int) = Action.async { implicit rs =>
        val queryTicketLastCalled = Tickets.filter(t =>
            (t.restaurantId === restaurantId.bind) && (t.ticketStatus === TicketStatus.CALLED.status))
            .sortBy(_.ticketNo.desc)
            .groupBy(_.ticketType)
            .map { case (ticketType, ticketRows) =>
                (ticketType, ticketRows.map(_.ticketNo).max)
            }

        val queryTicketLastTaken = Tickets.filter(t =>
            (t.restaurantId === restaurantId.bind) && (t.ticketStatus =!= TicketStatus.ARCHIVED.status))
            .sortBy(_.ticketNo.desc)
            .groupBy(_.ticketType)
            .map { case (ticketType, ticketRows) =>
                (ticketType, ticketRows.map(_.ticketNo).max)
            }

        val joinLastCalledLastTaken =
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

    def create = Action.async(parse.json) { implicit rs =>
        val sessionUserId = rs.session.get(Constants.SESSION_TOKEN_USER_ID).getOrElse("0")
        db.run(Users.filter(t => t.userId === sessionUserId.toInt).result.headOption).flatMap {
            case Some(_) =>
                rs.body.validate[TicketForm].map { form =>
                    if (form.createdById != sessionUserId.toInt) {
                        Future.successful(Unauthorized(Json.toJson(StatusResponse(StatusCode.UNAUTHORIZED.code, StatusCode.UNAUTHORIZED.message))))
                    }

                    val ticketType = TicketType.values
                        .filter(ticketType =>
                            ticketType.minSeatNo <= form.ticketSeatNo && ticketType.maxSeatNo >= form.ticketSeatNo)
                        .map(_.typeName).head

                    val queryTicketLastTakenNo = Tickets.filter(t =>
                        (t.restaurantId === form.restaurantId.bind) && (t.ticketStatus =!= TicketStatus.ARCHIVED.status) && (t.ticketType === ticketType.bind))
                        .sortBy(_.ticketNo.desc)
                        .map(_.ticketNo).max

                    val now = Timestamp.valueOf(LocalDateTime.now())
                    val newTicket = db.run(queryTicketLastTakenNo.result).map{
                        case Some(ticketNo) =>
                            TicketsRow(0, ticketNo + 1, form.restaurantId, now, form.createdById, form.ticketSeatNo, ticketType, TicketStatus.ACTIVE.toString)
                        case None =>
                            TicketsRow(0, 1, form.restaurantId, now, form.createdById, form.ticketSeatNo, ticketType, TicketStatus.ACTIVE.toString)
                    }

                    newTicket.flatMap { newTicketRow =>
                        db.run(Tickets += newTicketRow).map(_ =>
                            Ok
                        )
                    }
                }.recoverTotal { e =>
                    Future.successful(BadRequest(Json.toJson(StatusResponse(StatusCode.UNSUPPORTED_FORMAT.code, StatusCode.UNSUPPORTED_FORMAT.message))))
                }
            case None =>
                Future.successful(Unauthorized(Json.toJson(StatusResponse(StatusCode.UNAUTHORIZED.code, StatusCode.UNAUTHORIZED.message))))
        }
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

case class TicketForm(restaurantId: Int, createdById: Int, ticketSeatNo: Int, ticketStatus: String)

object TicketForm {
    implicit val ticketFormReads: Reads[TicketForm] = (
        (__ \ "restaurant_id").read[Int] and
        (__ \ "created_by_id").read[Int] and
        (__ \ "seat_no").read[Int](min(1) keepAnd max(12)) and
        (__ \ "ticket_status").read[String]
    )(TicketForm.apply _)
}