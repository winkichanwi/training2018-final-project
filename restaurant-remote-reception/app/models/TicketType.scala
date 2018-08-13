package models

import controllers.TicketForm

sealed abstract class TicketType(val typeName: String, val minSeatNo: Int, val maxSeatNo: Int) {
  override def toString: String = typeName
}

object TicketType {

  object A extends TicketType("A", 1, 2)

  object B extends TicketType("B", 3, 5)

  object C extends TicketType("C", 6, 8)

  object D extends TicketType("D", 9, 12)

  def types: Seq[TicketType] = Seq(A, B, C, D)

  def from(form: TicketForm): Option[TicketType] = types.find { ticketType =>
    ticketType.minSeatNo <= form.ticketSeatNo && ticketType.maxSeatNo >= form.ticketSeatNo
  }

}
