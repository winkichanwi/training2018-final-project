package controllers

import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import javax.inject.Inject
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.json.Writes
import play.api.libs.json.Reads._
import play.api.libs.json._
import play.api.libs.json.Json._
import play.api.libs.functional.syntax._
import play.api.mvc.{Action, Controller}
import slick.driver.JdbcProfile
import slick.driver.MySQLDriver.api._
import models.Tables._
import models._
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

import scala.concurrent.{ExecutionContext, Future}

/**
  * Controller for tickets
  */
class TicketController @Inject()(val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext)
    extends Controller with HasDatabaseConfigProvider[JdbcProfile]  {

    /**
      * [Authentication required]
      * Create ticket record with status set as active, ticket number based on last non-archived ticket number
      * @return Future[Result] Result of creating ticket
      */
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
                        (t.restaurantId === form.restaurantId.bind) && !t.ticketStatus.isEmpty && (t.ticketType === ticketType.bind))
                        .sortBy(_.ticketNo.desc)
                        .map(_.ticketNo).max
                        .result

                    val addTicketRowDBIO = for {
                        ticketNo <- queryTicketLastTakenNo
                        addTicketRow <- ticketNo match {
                            case Some(ticketNo) =>
                                Tickets += TicketsRow(0, ticketNo + 1, form.restaurantId, Timestamp.valueOf(LocalDateTime.now()),
                                    sessionUserId.toInt, form.ticketSeatNo, ticketType, TicketStatus.ACTIVE.status)
                            case None =>
                                Tickets += TicketsRow(0, 1, form.restaurantId, Timestamp.valueOf(LocalDateTime.now()),
                                    sessionUserId.toInt, form.ticketSeatNo, ticketType, TicketStatus.ACTIVE.status)
                        }
                    } yield addTicketRow

                    db.run(addTicketRowDBIO).map( _ => Ok )
                }.recoverTotal { e =>
                    Future.successful(BadRequest(Json.toJson(StatusResponse(StatusCode.UNSUPPORTED_FORMAT.code, StatusCode.UNSUPPORTED_FORMAT.message))))
                }
            case None =>
                Future.successful(Unauthorized(Json.toJson(StatusResponse(StatusCode.UNAUTHORIZED.code, StatusCode.UNAUTHORIZED.message))))
        }
    }

    /**
      * [Authentication required]
      * Update ticket status. Archived all non active tickets before updating status to avoid constraint conflict
      * @return Future[Result] Result of update
      */
    def update = Action.async(parse.json) { implicit rs =>
        val sessionUserId = rs.session.get(Constants.SESSION_TOKEN_USER_ID).getOrElse("0")
        db.run(Users.filter(t => t.userId === sessionUserId.toInt).result.headOption).flatMap {
            case Some(_) =>
                rs.body.validate[TicketStatusUpdateForm].map { form =>
                    if (TicketStatus.ACCEPTED.toString.equals(form.ticketStatus) || TicketStatus.CANCELLED.toString.equals(form.ticketStatus)) {
                        val updateDoneTickets = Tickets.filter(t =>
                            t.createdById === sessionUserId.toInt && !t.ticketStatus.isEmpty && t.ticketStatus =!= TicketStatus.ACTIVE.toString)
                            .map(_.ticketStatus)
                            .update(TicketStatus.NULL.status)

                        val updateTicketStatus = Tickets.filter(t => t.ticketId === form.ticketId.bind && t.createdById === sessionUserId.toInt)
                            .map(_.ticketStatus)
                            .update(Some(form.ticketStatus))

                        val updateTicketTransaction = (
                            for {
                                _ <- updateDoneTickets
                                _ <- updateTicketStatus
                            } yield ()
                            ).transactionally

                        db.run(updateTicketTransaction).map( _ => Ok)
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

    /**
      * [Authentication required]
      * Get logged in user reserved ticket's information
      * @return Future[Result] List of reserved tickets and related information
      */
    def getReservedTickets = Action.async { implicit rs =>
        val sessionUserId = rs.session.get(Constants.SESSION_TOKEN_USER_ID).getOrElse("0")
        val queryUserActiveTicket = Tickets.filter(t =>
            t.createdById === sessionUserId.toInt && !t.ticketStatus.isEmpty && (t.ticketStatus === TicketStatus.ACTIVE.toString))
            .sortBy(_.createdAt desc)
            .result
        db.run(Users.filter(t => t.userId === sessionUserId.toInt).result.headOption).flatMap {
            case Some(_) =>
                db.run(queryUserActiveTicket)
                    .map(_.map(row => UserTickets(row.ticketId, row.restaurantId, row.ticketType, row.ticketNo, row.ticketSeatNo, row.createdAt)))
                    .map(userTickets => Ok(Json.toJson(userTickets)))
            case None =>
                Future.successful(Unauthorized(Json.toJson(StatusResponse(StatusCode.UNAUTHORIZED.code, StatusCode.UNAUTHORIZED.message))))
        }
    }

    /**
      *
      * @param restaurantId
      * @return
      */
    def countTicketQueue(restaurantId: Int) = Action.async { implicit rs =>
        val sessionUserId = rs.session.get(Constants.SESSION_TOKEN_USER_ID).getOrElse("0")

        val groupTicketTypes = Tickets.filter(t =>
        (t.restaurantId === restaurantId.bind) && !t.ticketStatus.isEmpty && (t.ticketStatus === TicketStatus.ACTIVE.toString))
            .groupBy(_.ticketType)
            .map { case (ticketType, rows) => (ticketType, rows.length) }
            .result

        db.run(Users.filter(t => t.userId === sessionUserId.toInt).result.headOption).flatMap {
            case Some(_) =>
                db.run(groupTicketTypes)
                    .map(_.map(row => RestaurantTicketQueue(row._1, row._2)))
                    .map(queues => Ok(Json.toJson(queues)))
//                    .map(queues => Ok(Json.obj("restaurant_id" -> restaurantId, "ticket_counts" -> Json.toJson(queues))))
            case None =>
                Future.successful(Unauthorized(Json.toJson(StatusResponse(StatusCode.UNAUTHORIZED.code, StatusCode.UNAUTHORIZED.message))))
        }
    }

    def getLastCalled(restaurantId: Int, ticketType: String) = Action.async { implicit rs =>
        val sessionUserId = rs.session.get(Constants.SESSION_TOKEN_USER_ID).getOrElse("0")

        val queryTicketLastCalled = Tickets.filter(t =>
        (t.restaurantId === restaurantId.bind) &&
        ((!t.ticketStatus.isEmpty && t.ticketStatus === TicketStatus.ACCEPTED.toString) || (!t.ticketStatus.isEmpty && t.ticketStatus === TicketStatus.CANCELLED.toString)) &&
        (t.ticketType === ticketType))
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


    def getRestaurantReservedTickets(restaurantId: Int) = Action.async { implicit rs =>
        val sessionUserId = rs.session.get(Constants.SESSION_TOKEN_USER_ID).getOrElse("0")
        val queryUserRestaurantActiveTicket = Tickets.filter(t =>
            t.restaurantId === restaurantId.bind && t.createdById === sessionUserId.toInt && !t.ticketStatus.isEmpty && (t.ticketStatus === TicketStatus.ACTIVE.toString))
            .result
        db.run(Users.filter(t => t.userId === sessionUserId.toInt).result.headOption).flatMap {
            case Some(_) =>
                db.run(queryUserRestaurantActiveTicket)
            .map(_.map(row => UserTickets(row.ticketId, row.restaurantId, row.ticketType, row.ticketNo, row.ticketSeatNo, row.createdAt)))
            .map(userTickets => Ok(Json.toJson(userTickets)))
            case None =>
                Future.successful(Unauthorized(Json.toJson(StatusResponse(StatusCode.UNAUTHORIZED.code, StatusCode.UNAUTHORIZED.message))))
        }
    }
}



/**
  * Template for reading user ticket reservation form, used in create()
  * @param restaurantId ID of reserved restaurant
  * @param ticketSeatNo Number of seat to be reserved
  */
case class TicketForm(restaurantId: Int, ticketSeatNo: Int)

object TicketForm {
    implicit val ticketFormReads: Reads[TicketForm] = (
        (__ \ "restaurant_id").read[Int] and
        (__ \ "seat_no").read[Int](min(1) keepAnd max(12))
    )(TicketForm.apply _)
}

/**
  * Template for reading ticket status update form
  * @param ticketId ID of ticket to be updated
  * @param ticketStatus Status to be updated
  */
case class TicketStatusUpdateForm(ticketId: Int, ticketStatus: String)

object TicketStatusUpdateForm {
    implicit val ticketStatusUpdateFormReads: Reads[TicketStatusUpdateForm] = (
    (__ \ "ticket_id").read[Int] and
    (__ \ "ticket_status").read[String]
    )(TicketStatusUpdateForm.apply _)
}

/**
  * Template for writing ticket information
  * @param ticketId ID of ticket
  * @param restaurantId ID of reserved restaurant
  * @param ticketType Ticket type of ticket (A-D)
  * @param ticketNo Ticket number
  * @param seatNo Number of reserved seats
  * @param createdAt Creation time of ticket
  */
case class UserTickets(ticketId: Int, restaurantId: Int, ticketType: String, ticketNo: Int, seatNo: Int, createdAt: Timestamp)

object UserTickets {
    implicit val timestampWriter = new Writes[Timestamp] {
        override def writes(t: Timestamp): JsValue = JsString(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").format(t))
    }

    implicit val userTicketsWrites: Writes[UserTickets] = (
        (__ \ "ticket_id").write[Int] and
        (__ \ "restaurant_id").write[Int] and
        (__ \ "ticket_type").write[String] and
        (__ \ "ticket_no").write[Int] and
        (__ \ "seat_no").write[Int] and
        (__ \ "created_at").write[Timestamp]
    )(unlift(UserTickets.unapply))
}

/**
  *
  * @param ticketType
  * @param count
  */
case class RestaurantTicketQueue(ticketType: String, count: Int)

object RestaurantTicketQueue {
    implicit val restaurantTicketCountWrites: Writes[RestaurantTicketQueue] = (
    (__ \ "ticket_type").write[String] and
    (__ \ "ticket_count").write[Int]
    )(unlift(RestaurantTicketQueue.unapply))
}

case class RestaurantLastCalled(ticketType: String, lastCalled: Int)

object RestaurantLastCalled {
    implicit val restaurantLastCalledWrites: Writes[RestaurantLastCalled] = (
    (__ \ "ticket_type").write[String] and
    (__ \ "last_called").write[Int]
    )(unlift(RestaurantLastCalled.unapply))
}



