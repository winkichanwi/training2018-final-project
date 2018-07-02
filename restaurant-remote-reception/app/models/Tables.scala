package models
// AUTO-GENERATED Slick data model
/** Stand-alone Slick data model for immediate use */
object Tables extends {
  val profile = slick.driver.MySQLDriver
} with Tables

/** Slick data model trait for extension, choice of backend or usage in the cake pattern. (Make sure to initialize this late.) */
trait Tables {
  val profile: slick.driver.JdbcProfile
  import profile.api._
  import slick.model.ForeignKeyAction
  // NOTE: GetResult mappers for plain SQL are only generated for tables where Slick knows how to map the types of all columns.
  import slick.jdbc.{GetResult => GR}

  /** DDL for all tables. Call .create to execute. */
  lazy val schema = User.schema
  @deprecated("Use .schema instead of .ddl", "3.0")
  def ddl = schema

  /** Entity class storing rows of table User
   *  @param userId Database column USER_ID SqlType(INT), AutoInc, PrimaryKey
   *  @param userFullname Database column USER_FULLNAME SqlType(VARCHAR), Length(50,true)
   *  @param userEmail Database column USER_EMAIL SqlType(VARCHAR), Length(100,true) */
  case class UserRow(userId: Int, userFullname: String, userEmail: String)
  /** GetResult implicit for fetching UserRow objects using plain SQL queries */
  implicit def GetResultUserRow(implicit e0: GR[Int], e1: GR[String]): GR[UserRow] = GR{
    prs => import prs._
    UserRow.tupled((<<[Int], <<[String], <<[String]))
  }
  /** Table description of table USER. Objects of this class serve as prototypes for rows in queries. */
  class User(_tableTag: Tag) extends Table[UserRow](_tableTag, "USER") {
    def * = (userId, userFullname, userEmail) <> (UserRow.tupled, UserRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(userId), Rep.Some(userFullname), Rep.Some(userEmail))
        .shaped.<>({r=>import r._; _1.map(_=> UserRow.tupled((_1.get, _2.get, _3.get)))}, (_:Any) =>
      throw new Exception("Inserting into ? projection not supported."))

    /** Database column USER_ID SqlType(INT), AutoInc, PrimaryKey */
    val userId: Rep[Int] = column[Int]("USER_ID", O.AutoInc, O.PrimaryKey)
    /** Database column USER_FULLNAME SqlType(VARCHAR), Length(50,true) */
    val userFullname: Rep[String] = column[String]("USER_FULLNAME", O.Length(50,varying=true))
    /** Database column USER_EMAIL SqlType(VARCHAR), Length(100,true) */
    val userEmail: Rep[String] = column[String]("USER_EMAIL", O.Length(100,varying=true))
  }
  /** Collection-like TableQuery object for table User */
  lazy val User = new TableQuery(tag => new User(tag))
}
