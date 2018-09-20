package models

import org.scalatest.{Matchers, WordSpec}
import play.api.libs.json.Json
class StatusCodeTestSpec extends WordSpec with Matchers {

    "StatusCodeResponse" should {
        "create from createResponse" in {
            StatusCode.OK.genJsonResponse shouldBe Json.toJson(StatusResponse(2000, "OK"))
        }
    }
}
