package controllers

import models.Constants
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play._
import org.scalatestplus.play.guice.GuiceOneAppPerTest
import play.api.Mode

import play.api.db.{Database, Databases}
import play.api.test._
import play.api.test.Helpers._
import play.api.mvc.Result
import play.api.db.slick.DatabaseConfigProvider

import play.api.inject.Injector
import play.api.inject.guice.GuiceApplicationBuilder
import scala.concurrent.Future
import play.api.inject.bind

import scala.concurrent.ExecutionContext.Implicits.global

class AuthControllerSpec extends PlaySpec with GuiceOneAppPerTest {

    val database = Databases(
        driver = "com.mysql.jdbc.Driver",
        url = "jdbc:mysql://localhost:3306/restaurant_remote_reception_test",
        config = Map(
            "user" -> "root",
            "password" -> "mysql"
        )
    )

    implicit lazy val appBuilder: GuiceApplicationBuilder = new GuiceApplicationBuilder()
        .in(Mode.Test)
        .overrides(bind[Database].toInstance(database))
    lazy val injector: Injector = appBuilder.injector()
    lazy val dbConfProvider: DatabaseConfigProvider = injector.instanceOf[DatabaseConfigProvider]

    "authenticate" should {
        "should be valid if session user exists" in {
            val controller = new AuthController(dbConfProvider)

            val defaultUserId = "1"
            val request = FakeRequest().withSession(Constants.SESSION_TOKEN_USER_ID -> defaultUserId)
            val result: Future[Result] = controller.authenticate.apply(request)
            status(result) mustBe OK
        }

        "should be invalid if session user does not exists" in {
            val controller = new AuthController(dbConfProvider)

            val fakeUserId = "0"
            val request = FakeRequest().withSession(Constants.SESSION_TOKEN_USER_ID -> fakeUserId)
            val result: Future[Result] = controller.authenticate.apply(request)
            status(result) mustBe UNAUTHORIZED
        }
    }

}
