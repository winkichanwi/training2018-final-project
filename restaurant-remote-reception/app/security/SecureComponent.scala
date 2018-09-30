package security

import play.api.mvc._
import play.api.db.slick._
import slick.driver.JdbcProfile
import models.StatusCode
import repositories.UserRepository

import scala.concurrent.{ExecutionContext, Future}

trait SecureComponent extends SecureActionBuilder with SessionImplicits {
    self: Controller with HasDatabaseConfigProvider[JdbcProfile] =>
    def SecureAction(implicit ec: ExecutionContext): SecureActionComponent = new SecureActionComponent
}

trait SecureActionBuilder {
    self: Controller with HasDatabaseConfigProvider[JdbcProfile] with SessionImplicits =>

    protected val userRepo: UserRepository

    class SecureActionComponent(implicit ec: ExecutionContext) {
        def async(requestHandler: Request[AnyContent] => Future[Result]): Action[AnyContent] =
            async(BodyParsers.parse.anyContent)(requestHandler)

        def async[A](bodyParser: BodyParser[A])(requestHandler: Request[A] => Future[Result]): Action[A] =
            Action.async(bodyParser) { implicit rs =>
                db.run(userRepo.findById).flatMap {
                    case Some(_) =>
                        requestHandler(rs)
                    case None =>
                        Future.successful(Unauthorized(StatusCode.UNAUTHORIZED.genJsonResponse))
                }
            }
    }
}
