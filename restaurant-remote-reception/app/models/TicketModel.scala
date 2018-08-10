package models

object TicketStatus extends Enumeration {
    protected case class Val(status : Option[String], isWaiting: Boolean) extends super.Val {
        override def toString = status.getOrElse("Null")
    }

    implicit def valueToTicketStatusVal(x: Value): Val = x.asInstanceOf[Val]

    val ACTIVE = Val(Some("Active"), true)
    // val CALLED = Val("Called", true) // not implemented
    val CANCELLED = Val(Some("Cancelled"), false) // cancelled by either customer or restaurant
    val ACCEPTED = Val(Some("Accepted"), false) // accepted by restaurant
    // val ARCHIVED = Val(Some("Archived"), false) // archive when restaurant closes
    val NULL = Val(None, false) // TODO: hotfix at the moment for avoiding duplicate entries
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