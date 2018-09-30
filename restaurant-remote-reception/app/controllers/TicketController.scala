package controllers

import java.sql.Timestamp
import java.text.SimpleDateFormat
import javax.inject.Inject
import models._
import models.ticket._

import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.functional.syntax._
import play.api.libs.json.Json._
import play.api.libs.json.Reads._
import play.api.libs.json.{Writes, _}

import play.api.mvc.Controller
import repositories.{TicketRepository, UserRepository}
import security.SecureComponent
import slick.driver.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

/**
  * Controller for tickets
  */
class TicketController @Inject()(
    val userRepo: UserRepository,
    val ticketRepo: TicketRepository,
    val dbConfigProvider: DatabaseConfigProvider)(
    implicit ec: ExecutionContext)
    extends Controller with HasDatabaseConfigProvider[JdbcProfile] with SecureComponent {

    /**
      * [Authentication required]
      * Create ticket record with status set as active, ticket number based on last non-archived ticket number
      * @return Future[Result] Result of creating ticket
      */
    def create = SecureAction.async(parse.json) { implicit rs =>
        rs.body.validate[TicketForm].map { form =>
            TicketType.from(form).map(_.typeName) match {
                case Some(ticketType) =>
                    db.run(ticketRepo.create(form.restaurantId, ticketType, form.ticketSeatNo)).map( _ => Ok )
                case None =>
                    Future.successful(BadRequest(StatusCode.UNSUPPORTED_FORMAT.genJsonResponse))
            }
        }.recoverTotal { e =>
            Future.successful(BadRequest(StatusCode.UNSUPPORTED_FORMAT.genJsonResponse))
        }
    }

    /**
      * [Authentication required]
      * Update ticket status. Archived all non active tickets before updating status to avoid constraint conflict
      * @return Future[Result] Result of update
      */
    def update = SecureAction.async(parse.json) { implicit rs =>
        rs.body.validate[TicketStatusUpdateForm].map { form =>
            if (TicketStatus.ACCEPTED.toString.equals(form.ticketStatus) || TicketStatus.CANCELLED.toString.equals(form.ticketStatus)) {
                db.run(ticketRepo.update(form.ticketId, form.ticketStatus)).map( _ => Ok)
            } else {
                Future.successful(BadRequest(StatusCode.UNSUPPORTED_FORMAT.genJsonResponse))
            }
        }.recoverTotal { e =>
            Future.successful(BadRequest(StatusCode.UNSUPPORTED_FORMAT.genJsonResponse))
        }
    }

    /**
      * [Authentication required]
      * Get logged in user reserved ticket's information
      * @return Future[Result] List of reserved tickets and related information
      */
    def getUserReservedTickets = SecureAction.async { implicit rs =>
        db.run(ticketRepo.fetchUserActiveReservedTickets)
            .map(_.map(row => UserTickets(row.ticketId, row.restaurantId, row.ticketType, row.ticketNo, row.ticketSeatNo, row.createdAt)))
            .map(userTickets => Ok(Json.toJson(userTickets)))
    }

    /**
      * [Authentication required]
      * Counting the queue for each ticket type of the restaurant, initialise as 0
      * @param restaurantId ID of specified restaurant
      * @return Future[Result] List of ticket queue length
      */
    def fetchRestaurantQueue(restaurantId: Int) = SecureAction.async { implicit rs =>
        db.run(ticketRepo.fetchRestaurantQueues(restaurantId))
            .map(rows =>
                (rows ++ (TicketType.types.filterNot(ticketType =>
                    rows.map(_._1).contains(ticketType.typeName))
                    .map(ticketType => (ticketType.typeName, 0)))) // Initialize queue to 0 for ticket type without queue initialized
                .map(row => RestaurantTicketQueue(row._1, row._2))
            )
            .map(queues =>
                Ok(Json.obj("restaurant_id" -> restaurantId, "ticket_counts" -> Json.toJson(queues)))
            )
    }

    /**
      * [Authentication required]
      * Retrieve the last called ticket number of specfic restaurant for each ticket type
      * @param restaurantId ID of specified restaurant
      * @return Future[Result] List of last called ticket number of each ticket type
      */
    def getLastCalled(restaurantId: Int) = SecureAction.async { implicit rs =>
        db.run(ticketRepo.fetchLastCalled(restaurantId))
            .map(rows =>
                (rows ++ (TicketType.types.filterNot(ticketType =>
                rows.map(_._1).contains(ticketType.typeName))
                .map(ticketType => (ticketType.typeName, Some(0)))))
            .map(row => RestaurantLastCalled(row._1, row._2.getOrElse(0))))
            .map(lastCalled =>
                Ok(Json.obj("restaurant_id" -> restaurantId, "last_called_tickets" -> Json.toJson(lastCalled)))
            )
    }

    def getUserReservedTicketsByRestaurant(restaurantId: Int) = SecureAction.async { implicit rs =>
        db.run(ticketRepo.fetchUserActiveReservedTicketsByRestaurant(restaurantId))
            .map(_.map(row => UserTickets(row.ticketId, row.restaurantId, row.ticketType, row.ticketNo, row.ticketSeatNo, row.createdAt)))
            .map(userTickets => Ok(Json.toJson(userTickets)))
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



