package models

object TicketStatus extends Enumeration {
    protected case class Val(status : String, isWaiting: Boolean) extends super.Val {
        override def toString = status
    }

    implicit def valueToTicketStatusVal(x: Value): Val = x.asInstanceOf[Val]

    val ACTIVE = Val("Active", true)
    val CALLED = Val("Called", true)
    val CANCELLED = Val("Cancelled", false) // cancelled by either customer or restaurant
    val ACCEPTED = Val("Accepted", false) // accepted by restaurant
    val ARCHIVED = Val("Archived", false) // archive when restaurant closes

}

object TicketType extends Enumeration {
    protected case class Val(typeName : String, minSeatNo: Int, maxSeatNo: Int) extends super.Val {
        override def toString = typeName
    }

    implicit def valueToTicketTypeVal(x: Value): Val = x.asInstanceOf[Val]

    val A = Val("A", 1, 2)
    val B = Val("B", 3, 5)
    val C = Val("C", 6, 8)
    val D = Val("D", 9, 12)
}
