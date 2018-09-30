package repositories

import com.github.t3hnar.bcrypt._
import javax.inject.Inject
import models._
import models.Tables._
import play.api.db.slick._
import slick.driver.JdbcProfile
import slick.driver.MySQLDriver.api._

import scala.concurrent.ExecutionContext

class UserRepository @Inject()(val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) extends HasDatabaseConfigProvider[JdbcProfile] {

    def list = Users.sortBy(t => t.userId).result

    def create(userFullname: String, email: String, password: String) =
        for {
            userId <- (Users returning Users.map(_.userId)) += UsersRow(0, userFullname, email)
            result <- UserSecret += UserSecretRow(userId, password.bcrypt)
        } yield result

    def update(userId: Int, userFullname: String, email: String) = {
        val user = UsersRow(userId, userFullname, email)
        Users.filter(t => t.userId === user.userId.bind).update(user)
    }

    def findById(implicit sessionUserId: ID) = Users.filter(t => t.userId === sessionUserId.value).result.headOption

    def fetchInfoAndSecretByEmail(email: String) = Users.join(UserSecret).on(_.userId === _.userId)
        .filter { case (t1, t2) => t1.email === email }
        .result.headOption
}
