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

    def create = Action.async(parse.json) { implicit rs =>
        val sessionUserId = rs.session.get(Constants.SESSION_TOKEN_USER_ID).getOrElse("0")
        db.run(Users.filter(t => t.userId === sessionUserId.toInt).result.headOption).flatMap {
            case Some(_) =>
                rs.body.validate[TicketForm].map { form =>
                    val ticketType = TicketType.values
                        .filter(ticketType =>
                            ticketType.minSeatNo <= form.ticketSeatNo && ticketType.maxSeatNo >= form.ticketSeatNo)
                        .map(_.typeName).head

                    val queryTicketLastTakenNo = Tickets.filter(t =>
                        (t.restaurantId === form.restaurantId.bind) && (t.ticketStatus =!= TicketStatus.ARCHIVED.status) && (t.ticketType === ticketType.bind))
                        .sortBy(_.ticketNo.desc)
                        .map(_.ticketNo).max
                        .result

                    val now = Timestamp.valueOf(LocalDateTime.now())
                    val newTicket = db.run(queryTicketLastTakenNo).map {
                        case Some(ticketNo) =>
                            TicketsRow(0, ticketNo + 1, form.restaurantId, now, sessionUserId.toInt, form.ticketSeatNo, ticketType, TicketStatus.ACTIVE.toString)
                        case None =>
                            TicketsRow(0, 1, form.restaurantId, now, sessionUserId.toInt, form.ticketSeatNo, ticketType, TicketStatus.ACTIVE.toString)
                    }

                    newTicket.flatMap { newTicketRow =>
                        db.run(Tickets += newTicketRow).map(_ =>
                            Ok(Json.toJson(StatusResponse(StatusCode.OK.code, StatusCode.OK.message)))
                        )
                    }
                }.recoverTotal { e =>
                    Future.successful(BadRequest(Json.toJson(StatusResponse(StatusCode.UNSUPPORTED_FORMAT.code, StatusCode.UNSUPPORTED_FORMAT.message))))
                }
            case None =>
                Future.successful(Unauthorized(Json.toJson(StatusResponse(StatusCode.UNAUTHORIZED.code, StatusCode.UNAUTHORIZED.message))))
        }
    }

    def update = Action.async(parse.json) { implicit rs =>
        val sessionUserId = rs.session.get(Constants.SESSION_TOKEN_USER_ID).getOrElse("0")
        db.run(Users.filter(t => t.userId === sessionUserId.toInt).result.headOption).flatMap {
            case Some(_) =>
                rs.body.validate[TicketStatusUpdateForm].map { form =>
                    if (TicketStatus.values.map(_.toString).contains(form.ticketStatus)) {
                        val updateTicketStatus = Tickets.filter(t => t.ticketId === form.ticketId.bind && t.createdById === sessionUserId.toInt)
                            .map(t => t.ticketStatus)
                            .update(form.ticketStatus)
                        db.run(updateTicketStatus).map(_ =>
                            Ok(Json.toJson(StatusResponse(StatusCode.OK.code, StatusCode.OK.message)))
                        )
                    } else {
                        Future.successful(BadRequest(Json.toJson(StatusResponse(StatusCode.UNSUPPORTED_FORMAT.code, StatusCode.UNSUPPORTED_FORMAT.message))))
                    }
                }.recoverTotal { e =>
                    Future.successful(BadRequest(Json.toJson(StatusResponse(StatusCode.UNSUPPORTED_FORMAT.code, StatusCode.UNSUPPORTED_FORMAT.message))))
                }
            case None =>
                Future.successful(Unauthorized(Json.toJson(StatusResponse(StatusCode.UNAUTHORIZED.code, StatusCode.UNAUTHORIZED.message))))
        }
    }

    def countTickets(restaurantId: Int) = Action.async { implicit rs =>
        val sessionUserId = rs.session.get(Constants.SESSION_TOKEN_USER_ID).getOrElse("0")

        val groupTicketTypes = Tickets.filter(t =>
            (t.restaurantId === restaurantId.bind) && (t.ticketStatus === TicketStatus.ACTIVE.status))
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

    def getLastCalled(restaurantId: Int, ticketType: String) = Action.async { implicit rs =>
        val sessionUserId = rs.session.get(Constants.SESSION_TOKEN_USER_ID).getOrElse("0")

        val lastCalledTicketStatusSeq: Seq[String] = Seq(TicketStatus.ACCEPTED, TicketStatus.CANCELLED).map(_.toString)
        val queryTicketLastCalled = Tickets.filter(t =>
            (t.restaurantId === restaurantId.bind) && (t.ticketStatus inSet lastCalledTicketStatusSeq) && (t.ticketType === ticketType))
            .sortBy(_.ticketNo.desc)
            .map(_.ticketNo).max
            .result

        db.run(Users.filter(t => t.userId === sessionUserId.toInt).result.headOption).flatMap {
            case Some(_) =>
                db.run(queryTicketLastCalled).map {
                    case Some(lastCalledNo) =>
                        Ok(Json.toJson(RestaurantLastCalled(ticketType, lastCalledNo)))
                    case None =>
                        NotFound(Json.toJson(StatusResponse(StatusCode.RESOURCE_NOT_FOUND.code, StatusCode.RESOURCE_NOT_FOUND.message)))
                }
            case None =>
                Future.successful(Unauthorized(Json.toJson(StatusResponse(StatusCode.UNAUTHORIZED.code, StatusCode.UNAUTHORIZED.message))))
        }
    }


    def getReservedTickets = Action.async { implicit rs =>
        val sessionUserId = rs.session.get(Constants.SESSION_TOKEN_USER_ID).getOrElse("0")
        val queryUserActiveTicket = Tickets.filter(t =>
            t.createdById === sessionUserId.toInt && t.ticketStatus === TicketStatus.ACTIVE.status)
            .sortBy(_.createdAt desc)
            .result
        db.run(Users.filter(t => t.userId === sessionUserId.toInt).result.headOption).flatMap {
            case Some(_) =>
                db.run(queryUserActiveTicket)
                    .map(_.map(row => UserTickets(row.ticketId, row.restaurantId, row.ticketType, row.ticketSeatNo, row.ticketNo)))
                    .map(userTickets => Ok(Json.toJson(userTickets)))
            case None =>
                Future.successful(Unauthorized(Json.toJson(StatusResponse(StatusCode.UNAUTHORIZED.code, StatusCode.UNAUTHORIZED.message))))
        }
    }

    def getRestaurantReservedTickets(restaurantId: Int) = Action.async { implicit rs =>
        val sessionUserId = rs.session.get(Constants.SESSION_TOKEN_USER_ID).getOrElse("0")
        val queryUserRestaurantActiveTicket = Tickets.filter(t =>
            t.restaurantId === restaurantId.bind && t.createdById === sessionUserId.toInt && t.ticketStatus === TicketStatus.ACTIVE.status)
            .result
        db.run(Users.filter(t => t.userId === sessionUserId.toInt).result.headOption).flatMap {
            case Some(_) =>
                db.run(queryUserRestaurantActiveTicket)
                    .map(_.map(row => UserTickets(row.ticketId, row.restaurantId, row.ticketType, row.ticketSeatNo, row.ticketNo)))
                    .map(userTickets => Ok(Json.toJson(userTickets)))
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

case class RestaurantLastCalled(ticketType: String, lastCalled: Int)

object RestaurantLastCalled {
    implicit val restaurantLastCalledWrites: Writes[RestaurantLastCalled] = (
        (__ \ "ticket_type").write[String] and
            (__ \ "last_called").write[Int]
        )(unlift(RestaurantLastCalled.unapply))
}

case class TicketForm(restaurantId: Int, ticketSeatNo: Int)

object TicketForm {
    implicit val ticketFormReads: Reads[TicketForm] = (
        (__ \ "restaurant_id").read[Int] and
            (__ \ "seat_no").read[Int](min(1) keepAnd max(12))
        )(TicketForm.apply _)
}

case class TicketStatusUpdateForm(ticketId: Int, ticketStatus: String)

object TicketStatusUpdateForm {
    implicit val ticketStatusUpdateFormReads: Reads[TicketStatusUpdateForm] = (
        (__ \ "ticket_id").read[Int] and
        (__ \ "ticket_status").read[String]
        )(TicketStatusUpdateForm.apply _)
}

case class UserTickets(ticketId: Int, restaurantId: Int, ticketType: String, seatNo: Int, ticketNo: Int)

object UserTickets {
    implicit val userTicketsWrites: Writes[UserTickets] = (
        (__ \ "ticket_id").write[Int] and
        (__ \ "restaurant_id").write[Int] and
        (__ \ "ticket_type").write[String] and
        (__ \ "seat_no").write[Int] and
        (__ \ "ticket_no").write[Int]
    )(unlift(UserTickets.unapply))
}