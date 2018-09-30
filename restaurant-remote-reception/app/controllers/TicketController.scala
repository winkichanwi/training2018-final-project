package controllers

import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.time.LocalDateTime
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
                    TicketType.from(form).map(_.typeName) match {
                        case Some(ticketType) =>
                            val addTicketRowDBIO = for {
                                ticketNo <- Tickets.filter(t =>
                                            (t.restaurantId === form.restaurantId.bind) && !t.ticketStatus.isEmpty && (t.ticketType === ticketType.bind))
                                            .sortBy(_.ticketNo.desc)
                                            .map(_.ticketNo).max
                                            .result
                                addTicketRow <- ticketNo match {
                                    case Some(ticketNo) =>
                                        Tickets += TicketsRow(0, ticketNo + 1, form.restaurantId, Timestamp.valueOf(LocalDateTime.now()),
                                            sessionUserId.toInt, form.ticketSeatNo, ticketType, TicketStatus.ACTIVE.status)
                                    case None =>
                                        // Initial ticket no is 1
                                        Tickets += TicketsRow(0, 1, form.restaurantId, Timestamp.valueOf(LocalDateTime.now()),
                                            sessionUserId.toInt, form.ticketSeatNo, ticketType, TicketStatus.ACTIVE.status)
                                }
                            } yield addTicketRow

                            db.run(addTicketRowDBIO).map( _ => Ok )
                        case None =>
                            Future.successful(BadRequest(StatusCode.UNSUPPORTED_FORMAT.genJsonResponse))
                    }
                }.recoverTotal { e =>
                    Future.successful(BadRequest(StatusCode.UNSUPPORTED_FORMAT.genJsonResponse))
                }
            case None =>
                Future.successful(Unauthorized(StatusCode.UNAUTHORIZED.genJsonResponse))
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
                        val updateTicketTransaction = (
                            for {
                                _ <- Tickets.filter(t =>
                                        t.createdById === sessionUserId.toInt && !t.ticketStatus.isEmpty && t.ticketStatus =!= TicketStatus.ACTIVE.toString)
                                        .map(_.ticketStatus)
                                        .update(TicketStatus.NULL.status)
                                _ <- Tickets.filter(t =>
                                        t.ticketId === form.ticketId.bind && t.createdById === sessionUserId.toInt)
                                        .map(_.ticketStatus)
                                        .update(Some(form.ticketStatus))
                            } yield ()
                            ).transactionally
                        db.run(updateTicketTransaction).map( _ => Ok)
                    } else {
                        Future.successful(BadRequest(StatusCode.UNSUPPORTED_FORMAT.genJsonResponse))
                    }
                }.recoverTotal { e =>
                    Future.successful(BadRequest(StatusCode.UNSUPPORTED_FORMAT.genJsonResponse))
                }
            case None =>
                Future.successful(Unauthorized(StatusCode.UNAUTHORIZED.genJsonResponse))
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
                Future.successful(Unauthorized(StatusCode.UNAUTHORIZED.genJsonResponse))
        }
    }

    /**
      * [Authentication required]
      * Counting the queue for each ticket type of the restaurant, initialise as 0
      * @param restaurantId ID of specfied restaurant
      * @return Future[Result] List of ticket queue length
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
                    .map(rows =>
                        (rows ++ (TicketType.types.filterNot(ticketType =>
                            rows.map(_._1).contains(ticketType.typeName))
                            .map(ticketType => (ticketType.typeName, 0))))
                        .map(row => RestaurantTicketQueue(row._1, row._2))
                    )
                    .map(queues =>
                        Ok(Json.obj("restaurant_id" -> restaurantId, "ticket_counts" -> Json.toJson(queues)))
                    )
            case None =>
                Future.successful(Unauthorized(StatusCode.UNAUTHORIZED.genJsonResponse))
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
                        NotFound(StatusCode.RESOURCE_NOT_FOUND.genJsonResponse)
                }
            case None =>
                Future.successful(Unauthorized(StatusCode.UNAUTHORIZED.genJsonResponse))
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
                Future.successful(Unauthorized(StatusCode.UNAUTHORIZED.genJsonResponse))
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
  * Template for writing ticket queues
  * @param ticketType Ticket type of ticket (A-D)
  * @param count Number indicating the length of queue
  */
case class RestaurantTicketQueue(ticketType: String, count: Int)

object RestaurantTicketQueue {
    implicit val restaurantTicketCountWrites: Writes[RestaurantTicketQueue] = (
    (__ \ "ticket_type").write[String] and
    (__ \ "count").write[Int]
    )(unlift(RestaurantTicketQueue.unapply))
}

case class RestaurantLastCalled(ticketType: String, lastCalled: Int)

object RestaurantLastCalled {
    implicit val restaurantLastCalledWrites: Writes[RestaurantLastCalled] = (
    (__ \ "ticket_type").write[String] and
    (__ \ "last_called").write[Int]
    )(unlift(RestaurantLastCalled.unapply))
}



