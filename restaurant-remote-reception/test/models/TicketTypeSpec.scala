package models

import controllers.TicketForm
import org.scalatest.{Matchers, WordSpec}

class TicketTypeSpec extends WordSpec with Matchers {

  "TicketType" should {

    "create from form" in {
      TicketType.from(TicketForm(111, 1)) shouldBe Some(TicketType.A)
      TicketType.from(TicketForm(111, 3)) shouldBe Some(TicketType.B)
      TicketType.from(TicketForm(111, 6)) shouldBe Some(TicketType.C)
      TicketType.from(TicketForm(111, 9)) shouldBe Some(TicketType.D)
      TicketType.from(TicketForm(111, 0)) shouldBe None
      TicketType.from(TicketForm(111, 20)) shouldBe None
    }
  }

}
