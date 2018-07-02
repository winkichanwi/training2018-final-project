package dao

import javax.inject.Inject
import models.User
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile
import javax.inject.Singleton
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UserDAOImpl @Inject()(dbConfigProvider: DatabaseConfigProvider) (implicit ec: ExecutionContext) extends UserDAO {

    protected val dbConfig = dbConfigProvider.get[JdbcProfile]

    import dbConfig._
    import driver.api._

    class UserTable(tag:Tag)
        extends Table[User](tag, "USER") {
        def id = column[Long]("USER_ID", O.PrimaryKey,O.AutoInc)
        def fullName = column[String]("USER_FULLNAME")
        def email = column[String]("USER_EMAIL")

        override def * =
            (id, fullName, email) <> (User.tupled, User.unapply)
    }

    implicit val users = TableQuery[UserTable]

    override def add(user: User): Future[String] = {
        db.run(users += user).map(res => "User successfully added").recover {
            case ex : Exception => ex.getCause.getMessage
        }
    }

    override def delete(id: Long): Future[Int] = {
        db.run(users.filter(_.id === id).delete)
    }

    override def get(id: Long): Future[Option[User]] = {
        db.run(users.filter(_.id === id).result.headOption)
    }

    override def listAll: Future[Seq[User]] = {
        db.run(users.result)
    }
}