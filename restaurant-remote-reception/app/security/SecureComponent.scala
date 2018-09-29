package security

import models.Tables.Users
import play.api.mvc._
import play.api.db.slick._
import slick.driver.JdbcProfile
import models.{Constants, StatusCode}
import slick.driver.MySQLDriver.api._

import scala.concurrent.{ExecutionContext, Future}

trait SecureComponent extends SecureActionBuilder {
    self: Controller with HasDatabaseConfigProvider[JdbcProfile] =>
    def SecureAction(implicit ec: ExecutionContext): SecureActionComponent = new SecureActionComponent
}

trait SecureActionBuilder {
    self: Controller with HasDatabaseConfigProvider[JdbcProfile] =>

    class SecureActionComponent(implicit ec: ExecutionContext) {
        def async(requestHandler: Request[AnyContent] => Future[Result]): Action[AnyContent] =
            async(BodyParsers.parse.anyContent)(requestHandler)

        def async[A](bodyParser: BodyParser[A])(requestHandler: Request[A] => Future[Result]): Action[A] =
            Action.async(bodyParser) { rs =>
                val sessionUserId = rs.session.get(Constants.SESSION_TOKEN_USER_ID).getOrElse("0")
                db.run(Users.filter(t => t.userId === sessionUserId.toInt).result.headOption).flatMap {
                    case Some(_) =>
                        requestHandler(rs)
                    case None =>
                        Future.successful(Unauthorized(StatusCode.UNAUTHORIZED.genJsonResponse))
                }
            }
    }
}
