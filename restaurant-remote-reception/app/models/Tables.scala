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
  lazy val schema = Restaurants.schema ++ ShoppingCenters.schema ++ Tickets.schema ++ Users.schema ++ UserSecret.schema
  @deprecated("Use .schema instead of .ddl", "3.0")
  def ddl = schema

  /** Entity class storing rows of table Restaurants
   *  @param restaurantId Database column RESTAURANT_ID SqlType(INT), AutoInc, PrimaryKey
   *  @param restaurantName Database column RESTAURANT_NAME SqlType(VARCHAR), Length(100,true)
   *  @param shoppingCenterId Database column SHOPPING_CENTER_ID SqlType(INT)
   *  @param restaurantStatus Database column RESTAURANT_STATUS SqlType(VARCHAR), Length(20,true), Default(Closed)
   *  @param floor Database column FLOOR SqlType(VARCHAR), Length(10,true)
   *  @param seatNo Database column SEAT_NO SqlType(INT)
   *  @param cuisine Database column CUISINE SqlType(VARCHAR), Length(100,true)
   *  @param phoneNo Database column PHONE_NO SqlType(VARCHAR), Length(20,true)
   *  @param openingHour Database column OPENING_HOUR SqlType(VARCHAR), Length(100,true)
   *  @param imageUrl Database column IMAGE_URL SqlType(VARCHAR), Length(300,true), Default(None) */
  case class RestaurantsRow(restaurantId: Int, restaurantName: String, shoppingCenterId: Int, restaurantStatus: String = "Closed", floor: String, seatNo: Int, cuisine: String, phoneNo: String, openingHour: String, imageUrl: Option[String] = None)
  /** GetResult implicit for fetching RestaurantsRow objects using plain SQL queries */
  implicit def GetResultRestaurantsRow(implicit e0: GR[Int], e1: GR[String], e2: GR[Option[String]]): GR[RestaurantsRow] = GR{
    prs => import prs._
    RestaurantsRow.tupled((<<[Int], <<[String], <<[Int], <<[String], <<[String], <<[Int], <<[String], <<[String], <<[String], <<?[String]))
  }
  /** Table description of table RESTAURANTS. Objects of this class serve as prototypes for rows in queries. */
  class Restaurants(_tableTag: Tag) extends Table[RestaurantsRow](_tableTag, "RESTAURANTS") {
    def * = (restaurantId, restaurantName, shoppingCenterId, restaurantStatus, floor, seatNo, cuisine, phoneNo, openingHour, imageUrl) <> (RestaurantsRow.tupled, RestaurantsRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(restaurantId), Rep.Some(restaurantName), Rep.Some(shoppingCenterId), Rep.Some(restaurantStatus), Rep.Some(floor), Rep.Some(seatNo), Rep.Some(cuisine), Rep.Some(phoneNo), Rep.Some(openingHour), imageUrl).shaped.<>({r=>import r._; _1.map(_=> RestaurantsRow.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get, _7.get, _8.get, _9.get, _10)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column RESTAURANT_ID SqlType(INT), AutoInc, PrimaryKey */
    val restaurantId: Rep[Int] = column[Int]("RESTAURANT_ID", O.AutoInc, O.PrimaryKey)
    /** Database column RESTAURANT_NAME SqlType(VARCHAR), Length(100,true) */
    val restaurantName: Rep[String] = column[String]("RESTAURANT_NAME", O.Length(100,varying=true))
    /** Database column SHOPPING_CENTER_ID SqlType(INT) */
    val shoppingCenterId: Rep[Int] = column[Int]("SHOPPING_CENTER_ID")
    /** Database column RESTAURANT_STATUS SqlType(VARCHAR), Length(20,true), Default(Closed) */
    val restaurantStatus: Rep[String] = column[String]("RESTAURANT_STATUS", O.Length(20,varying=true), O.Default("Closed"))
    /** Database column FLOOR SqlType(VARCHAR), Length(10,true) */
    val floor: Rep[String] = column[String]("FLOOR", O.Length(10,varying=true))
    /** Database column SEAT_NO SqlType(INT) */
    val seatNo: Rep[Int] = column[Int]("SEAT_NO")
    /** Database column CUISINE SqlType(VARCHAR), Length(100,true) */
    val cuisine: Rep[String] = column[String]("CUISINE", O.Length(100,varying=true))
    /** Database column PHONE_NO SqlType(VARCHAR), Length(20,true) */
    val phoneNo: Rep[String] = column[String]("PHONE_NO", O.Length(20,varying=true))
    /** Database column OPENING_HOUR SqlType(VARCHAR), Length(100,true) */
    val openingHour: Rep[String] = column[String]("OPENING_HOUR", O.Length(100,varying=true))
    /** Database column IMAGE_URL SqlType(VARCHAR), Length(300,true), Default(None) */
    val imageUrl: Rep[Option[String]] = column[Option[String]]("IMAGE_URL", O.Length(300,varying=true), O.Default(None))

    /** Foreign key referencing ShoppingCenters (database name RESTAURANTS_ibfk_1) */
    lazy val shoppingCentersFk = foreignKey("RESTAURANTS_ibfk_1", shoppingCenterId, ShoppingCenters)(r => r.shoppingCenterId, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)

    /** Uniqueness Index over (phoneNo) (database name PHONE_NO) */
    val index1 = index("PHONE_NO", phoneNo, unique=true)
    /** Uniqueness Index over (restaurantName,shoppingCenterId) (database name restaurant_shopping_center_UNIQUE) */
    val index2 = index("restaurant_shopping_center_UNIQUE", (restaurantName, shoppingCenterId), unique=true)
  }
  /** Collection-like TableQuery object for table Restaurants */
  lazy val Restaurants = new TableQuery(tag => new Restaurants(tag))

  /** Entity class storing rows of table ShoppingCenters
   *  @param shoppingCenterId Database column SHOPPING_CENTER_ID SqlType(INT), AutoInc, PrimaryKey
   *  @param shoppingCenterName Database column SHOPPING_CENTER_NAME SqlType(VARCHAR), Length(100,true)
   *  @param branchName Database column BRANCH_NAME SqlType(VARCHAR), Length(100,true), Default(Some()) */
  case class ShoppingCentersRow(shoppingCenterId: Int, shoppingCenterName: String, branchName: Option[String] = Some(""))
  /** GetResult implicit for fetching ShoppingCentersRow objects using plain SQL queries */
  implicit def GetResultShoppingCentersRow(implicit e0: GR[Int], e1: GR[String], e2: GR[Option[String]]): GR[ShoppingCentersRow] = GR{
    prs => import prs._
    ShoppingCentersRow.tupled((<<[Int], <<[String], <<?[String]))
  }
  /** Table description of table SHOPPING_CENTERS. Objects of this class serve as prototypes for rows in queries. */
  class ShoppingCenters(_tableTag: Tag) extends Table[ShoppingCentersRow](_tableTag, "SHOPPING_CENTERS") {
    def * = (shoppingCenterId, shoppingCenterName, branchName) <> (ShoppingCentersRow.tupled, ShoppingCentersRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(shoppingCenterId), Rep.Some(shoppingCenterName), branchName).shaped.<>({r=>import r._; _1.map(_=> ShoppingCentersRow.tupled((_1.get, _2.get, _3)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column SHOPPING_CENTER_ID SqlType(INT), AutoInc, PrimaryKey */
    val shoppingCenterId: Rep[Int] = column[Int]("SHOPPING_CENTER_ID", O.AutoInc, O.PrimaryKey)
    /** Database column SHOPPING_CENTER_NAME SqlType(VARCHAR), Length(100,true) */
    val shoppingCenterName: Rep[String] = column[String]("SHOPPING_CENTER_NAME", O.Length(100,varying=true))
    /** Database column BRANCH_NAME SqlType(VARCHAR), Length(100,true), Default(Some()) */
    val branchName: Rep[Option[String]] = column[Option[String]]("BRANCH_NAME", O.Length(100,varying=true), O.Default(Some("")))

    /** Uniqueness Index over (shoppingCenterName,branchName) (database name shopping_center_branch_UNIQUE) */
    val index1 = index("shopping_center_branch_UNIQUE", (shoppingCenterName, branchName), unique=true)
  }
  /** Collection-like TableQuery object for table ShoppingCenters */
  lazy val ShoppingCenters = new TableQuery(tag => new ShoppingCenters(tag))

  /** Entity class storing rows of table Tickets
   *  @param ticketId Database column TICKET_ID SqlType(INT), AutoInc, PrimaryKey
   *  @param ticketNo Database column TICKET_NO SqlType(INT)
   *  @param restaurantId Database column RESTAURANT_ID SqlType(INT)
   *  @param createdAt Database column CREATED_AT SqlType(DATETIME)
   *  @param createdById Database column CREATED_BY_ID SqlType(INT)
   *  @param ticketSeatNo Database column TICKET_SEAT_NO SqlType(INT)
   *  @param ticketType Database column TICKET_TYPE SqlType(VARCHAR), Length(20,true)
   *  @param ticketStatus Database column TICKET_STATUS SqlType(VARCHAR), Length(20,true), Default(Active) */
  case class TicketsRow(ticketId: Int, ticketNo: Int, restaurantId: Int, createdAt: java.sql.Timestamp, createdById: Int, ticketSeatNo: Int, ticketType: String, ticketStatus: String = "Active")
  /** GetResult implicit for fetching TicketsRow objects using plain SQL queries */
  implicit def GetResultTicketsRow(implicit e0: GR[Int], e1: GR[java.sql.Timestamp], e2: GR[String]): GR[TicketsRow] = GR{
    prs => import prs._
    TicketsRow.tupled((<<[Int], <<[Int], <<[Int], <<[java.sql.Timestamp], <<[Int], <<[Int], <<[String], <<[String]))
  }
  /** Table description of table TICKETS. Objects of this class serve as prototypes for rows in queries. */
  class Tickets(_tableTag: Tag) extends Table[TicketsRow](_tableTag, "TICKETS") {
    def * = (ticketId, ticketNo, restaurantId, createdAt, createdById, ticketSeatNo, ticketType, ticketStatus) <> (TicketsRow.tupled, TicketsRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(ticketId), Rep.Some(ticketNo), Rep.Some(restaurantId), Rep.Some(createdAt), Rep.Some(createdById), Rep.Some(ticketSeatNo), Rep.Some(ticketType), Rep.Some(ticketStatus)).shaped.<>({r=>import r._; _1.map(_=> TicketsRow.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get, _7.get, _8.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column TICKET_ID SqlType(INT), AutoInc, PrimaryKey */
    val ticketId: Rep[Int] = column[Int]("TICKET_ID", O.AutoInc, O.PrimaryKey)
    /** Database column TICKET_NO SqlType(INT) */
    val ticketNo: Rep[Int] = column[Int]("TICKET_NO")
    /** Database column RESTAURANT_ID SqlType(INT) */
    val restaurantId: Rep[Int] = column[Int]("RESTAURANT_ID")
    /** Database column CREATED_AT SqlType(DATETIME) */
    val createdAt: Rep[java.sql.Timestamp] = column[java.sql.Timestamp]("CREATED_AT")
    /** Database column CREATED_BY_ID SqlType(INT) */
    val createdById: Rep[Int] = column[Int]("CREATED_BY_ID")
    /** Database column TICKET_SEAT_NO SqlType(INT) */
    val ticketSeatNo: Rep[Int] = column[Int]("TICKET_SEAT_NO")
    /** Database column TICKET_TYPE SqlType(VARCHAR), Length(20,true) */
    val ticketType: Rep[String] = column[String]("TICKET_TYPE", O.Length(20,varying=true))
    /** Database column TICKET_STATUS SqlType(VARCHAR), Length(20,true), Default(Active) */
    val ticketStatus: Rep[String] = column[String]("TICKET_STATUS", O.Length(20,varying=true), O.Default("Active"))

    /** Foreign key referencing Restaurants (database name TICKETS_ibfk_1) */
    lazy val restaurantsFk = foreignKey("TICKETS_ibfk_1", restaurantId, Restaurants)(r => r.restaurantId, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    /** Foreign key referencing Users (database name TICKETS_ibfk_2) */
    lazy val usersFk = foreignKey("TICKETS_ibfk_2", createdById, Users)(r => r.userId, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)

    /** Uniqueness Index over (restaurantId,createdById,ticketStatus,ticketSeatNo) (database name restaurant_user_ticket_status__seat_UNIQUE) */
    val index1 = index("restaurant_user_ticket_status__seat_UNIQUE", (restaurantId, createdById, ticketStatus, ticketSeatNo), unique=true)
  }
  /** Collection-like TableQuery object for table Tickets */
  lazy val Tickets = new TableQuery(tag => new Tickets(tag))

  /** Entity class storing rows of table Users
   *  @param userId Database column USER_ID SqlType(INT), AutoInc, PrimaryKey
   *  @param userFullname Database column USER_FULLNAME SqlType(VARCHAR), Length(50,true)
   *  @param email Database column EMAIL SqlType(VARCHAR), Length(100,true) */
  case class UsersRow(userId: Int, userFullname: String, email: String)
  /** GetResult implicit for fetching UsersRow objects using plain SQL queries */
  implicit def GetResultUsersRow(implicit e0: GR[Int], e1: GR[String]): GR[UsersRow] = GR{
    prs => import prs._
    UsersRow.tupled((<<[Int], <<[String], <<[String]))
  }
  /** Table description of table USERS. Objects of this class serve as prototypes for rows in queries. */
  class Users(_tableTag: Tag) extends Table[UsersRow](_tableTag, "USERS") {
    def * = (userId, userFullname, email) <> (UsersRow.tupled, UsersRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(userId), Rep.Some(userFullname), Rep.Some(email)).shaped.<>({r=>import r._; _1.map(_=> UsersRow.tupled((_1.get, _2.get, _3.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column USER_ID SqlType(INT), AutoInc, PrimaryKey */
    val userId: Rep[Int] = column[Int]("USER_ID", O.AutoInc, O.PrimaryKey)
    /** Database column USER_FULLNAME SqlType(VARCHAR), Length(50,true) */
    val userFullname: Rep[String] = column[String]("USER_FULLNAME", O.Length(50,varying=true))
    /** Database column EMAIL SqlType(VARCHAR), Length(100,true) */
    val email: Rep[String] = column[String]("EMAIL", O.Length(100,varying=true))

    /** Uniqueness Index over (email) (database name email_UNIQUE) */
    val index1 = index("email_UNIQUE", email, unique=true)
  }
  /** Collection-like TableQuery object for table Users */
  lazy val Users = new TableQuery(tag => new Users(tag))

  /** Entity class storing rows of table UserSecret
   *  @param userId Database column USER_ID SqlType(INT), PrimaryKey
   *  @param password Database column PASSWORD SqlType(VARCHAR), Length(300,true) */
  case class UserSecretRow(userId: Int, password: String)
  /** GetResult implicit for fetching UserSecretRow objects using plain SQL queries */
  implicit def GetResultUserSecretRow(implicit e0: GR[Int], e1: GR[String]): GR[UserSecretRow] = GR{
    prs => import prs._
    UserSecretRow.tupled((<<[Int], <<[String]))
  }
  /** Table description of table USER_SECRET. Objects of this class serve as prototypes for rows in queries. */
  class UserSecret(_tableTag: Tag) extends Table[UserSecretRow](_tableTag, "USER_SECRET") {
    def * = (userId, password) <> (UserSecretRow.tupled, UserSecretRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(userId), Rep.Some(password)).shaped.<>({r=>import r._; _1.map(_=> UserSecretRow.tupled((_1.get, _2.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column USER_ID SqlType(INT), PrimaryKey */
    val userId: Rep[Int] = column[Int]("USER_ID", O.PrimaryKey)
    /** Database column PASSWORD SqlType(VARCHAR), Length(300,true) */
    val password: Rep[String] = column[String]("PASSWORD", O.Length(300,varying=true))

    /** Foreign key referencing Users (database name USER_SECRET_ibfk_1) */
    lazy val usersFk = foreignKey("USER_SECRET_ibfk_1", userId, Users)(r => r.userId, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
  }
  /** Collection-like TableQuery object for table UserSecret */
  lazy val UserSecret = new TableQuery(tag => new UserSecret(tag))
}
