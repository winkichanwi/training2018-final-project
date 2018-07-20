package controllers

import javax.inject._
import play.api.libs.json.Json
import play.api.mvc._

/**
  * This controller creates an `Action` to handle HTTP requests to have a health check on the application
  */
@Singleton
class HealthCheckController @Inject() extends Controller {

    def healthCheck = Action { implicit request =>
        Ok(Json.obj("message" -> "Request is received successfully."))
    }
}
