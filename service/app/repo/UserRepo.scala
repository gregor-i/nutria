package repo

import java.util.UUID

import anorm.{RowParser, SqlParser, _}
import javax.inject.{Inject, Singleton}
import module.auth.GoogleUserInfo
import nutria.core.User
import play.api.db.Database

@Singleton()
class UserRepo @Inject() (db: Database) {
  private val rowParser: RowParser[User] = for {
    id           <- SqlParser.str("id")
    name         <- SqlParser.str("name")
    email        <- SqlParser.str("email")
    googleUserId <- SqlParser.str("google_user_id").?
  } yield User(id, name, email, googleUserId)

  def get(id: String): Option[User] =
    db.withConnection { implicit con =>
      SQL"""SELECT *
            FROM users
            WHERE id = $id"""
        .as(rowParser.singleOpt)
    }

  def getWithGoogleId(googleId: String): Option[User] =
    db.withConnection { implicit con =>
      SQL"""SELECT *
            FROM users
            WHERE google_user_id = $googleId"""
        .as(rowParser.singleOpt)
    }

  def save(row: User): Unit =
    db.withConnection { implicit con =>
      SQL"""INSERT INTO users (id, name, email, google_user_id)
            VALUES (${row.id}, ${row.name}, ${row.email}, ${row.googleUserId})
            ON CONFLICT (id) DO UPDATE
            SET name = ${row.name},
              email = ${row.email},
              google_user_id = ${row.googleUserId}
        """.executeUpdate()
    }

  def upsertWithGoogleData(userInfo: GoogleUserInfo): User = {
    val user = getWithGoogleId(userInfo.id) match {
      case Some(user) =>
        user.copy(name = userInfo.name, email = userInfo.email)
      case None =>
        val id = UUID.randomUUID().toString
        User(id, userInfo.name, userInfo.email, Some(userInfo.id))
    }
    save(user)
    user
  }

  def delete(id: String): Int =
    db.withConnection { implicit con =>
      SQL"""DELETE FROM users
            WHERE id = $id"""
        .executeUpdate()
    }
}
