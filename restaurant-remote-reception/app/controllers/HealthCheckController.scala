package controllers

import javax.inject._
import play.api.mvc._
import utils.Foo

/**
  * This controller creates an `Action` to handle HTTP requests to have a health check on the application
  */
@Singleton
class HealthCheckController @Inject()(@Named("FooA") val foo: Foo) extends Controller {

    def healthCheck = Action { implicit request =>
        Ok(foo.getMsg)
    }
}
