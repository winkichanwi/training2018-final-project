package repositories

import java.sql.Timestamp
import java.time.LocalDateTime

import models.Tables._
import javax.inject.Inject
import models.ID
import models.Tables.Tickets
import models.ticket.TicketStatus
import play.api.db.slick._
import slick.driver.JdbcProfile
import slick.driver.MySQLDriver.api._

import scala.concurrent.ExecutionContext

class TicketRepository @Inject()
    (val dbConfigProvider: DatabaseConfigProvider)
    (implicit ec: ExecutionContext)
    extends HasDatabaseConfigProvider[JdbcProfile] {

    private def findlastTicketNo(restaurantId: Int, ticketType: String) =
        Tickets.filter(t =>
            (t.restaurantId === restaurantId.bind) && !t.ticketStatus.isEmpty && (t.ticketType === ticketType.bind))
            .sortBy(_.ticketNo.desc)
            .map(_.ticketNo).max
            .result

    private def createWithTicketNo(lastTicketNo: Int, restaurantId: Int, ticketSeatNo: Int, ticketType: String, sessionUserId: ID) =
        Tickets += TicketsRow(
            ticketId = 0,
            ticketNo = lastTicketNo + 1,
            restaurantId = restaurantId,
            createdAt = Timestamp.valueOf(LocalDateTime.now()),
            createdById = sessionUserId.value,
            ticketSeatNo = ticketSeatNo,
            ticketType = ticketType,
            ticketStatus = TicketStatus.ACTIVE.status)

    def create(restaurantId: Int, ticketType: String, ticketSeatNo: Int)(implicit sessionUserId: ID) =
        for {
            ticketNo <- findlastTicketNo(restaurantId, ticketType)
            addTicketRow <- ticketNo match {
                case Some(ticketNo) =>
                    createWithTicketNo(ticketNo, restaurantId, ticketSeatNo, ticketType, sessionUserId)
                case None =>
                    // Initial ticket no is 1
                    createWithTicketNo(0, restaurantId, ticketSeatNo, ticketType, sessionUserId)
            }
        } yield addTicketRow

    def update(ticketId: Int, ticketStatus: String)(implicit sessionUserId: ID) = (
            for {
                _ <- Tickets.filter(t =>
                    t.createdById === sessionUserId.value && !t.ticketStatus.isEmpty && t.ticketStatus =!= TicketStatus.ACTIVE.toString)
                    .map(_.ticketStatus)
                    .update(TicketStatus.NULL.status)
                _ <- Tickets.filter(t =>
                    t.ticketId === ticketId.bind && t.createdById === sessionUserId.value)
                    .map(_.ticketStatus)
                    .update(Some(ticketStatus))
            } yield ()
        ).transactionally

    def fetchUserActiveReservedTickets(implicit sessionUserId: ID) =
        Tickets.filter(t =>
        t.createdById === sessionUserId.value && !t.ticketStatus.isEmpty && (t.ticketStatus === TicketStatus.ACTIVE.toString))
        .sortBy(_.createdAt desc)
        .result

    def fetchRestaurantQueues(restaurantId: Int) =
        Tickets.filter(t =>
            (t.restaurantId === restaurantId.bind) && !t.ticketStatus.isEmpty && (t.ticketStatus === TicketStatus.ACTIVE.toString))
            .groupBy(_.ticketType)
            .map { case (ticketType, rows) => (ticketType, rows.length) }
            .result

    def fetchLastCalled(restaurantId: Int, ticketType: String) =
        Tickets.filter(t =>
        (t.restaurantId === restaurantId.bind) &&
            ((!t.ticketStatus.isEmpty && t.ticketStatus === TicketStatus.ACCEPTED.toString) ||
                (!t.ticketStatus.isEmpty && t.ticketStatus === TicketStatus.CANCELLED.toString)) &&
            (t.ticketType === ticketType))
        .sortBy(_.ticketNo.desc)
        .map(_.ticketNo).max
        .result

    def fetchUserActiveReservedTicketsByRestaurant(restaurantId: Int)(implicit sessionUserId: ID) =
        Tickets.filter(t =>
                t.restaurantId === restaurantId.bind && t.createdById === sessionUserId.value &&
                !t.ticketStatus.isEmpty &&
                (t.ticketStatus === TicketStatus.ACTIVE.toString))
            .result
}
