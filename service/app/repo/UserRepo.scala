package repo

import java.util.UUID

import anorm.{RowParser, SqlParser, _}
import javax.inject.{Inject, Singleton}
import module.auth.GoogleUserInfo
import nutria.core.User
import play.api.db.Database

case class UserRow(user: User, googleUserId: Option[String])

@Singleton()
class UserRepo @Inject() (db: Database) {
  private val rowParser: RowParser[UserRow] = for {
    id           <- SqlParser.str("id")
    name         <- SqlParser.str("name")
    email        <- SqlParser.str("email")
    picture      <- SqlParser.str("picture")
    googleUserId <- SqlParser.str("google_user_id").?
  } yield UserRow(User(id, name, email, picture), googleUserId)

  def get(id: String): Option[UserRow] =
    db.withConnection { implicit con =>
      SQL"""SELECT *
            FROM users
            WHERE id = $id"""
        .as(rowParser.singleOpt)
    }

  def getWithGoogleId(googleId: String): Option[UserRow] =
    db.withConnection { implicit con =>
      SQL"""SELECT *
            FROM users
            WHERE google_user_id = $googleId"""
        .as(rowParser.singleOpt)
    }

  def save(row: UserRow): Unit =
    db.withConnection { implicit con =>
      SQL"""INSERT INTO users (id, name, email, picture, google_user_id)
            VALUES (${row.user.id}, ${row.user.name}, ${row.user.email}, ${row.user.picture}, ${row.googleUserId})
            ON CONFLICT (id) DO UPDATE
            SET name = ${row.user.name},
              email = ${row.user.email},
              picture = ${row.user.picture},
              google_user_id = ${row.googleUserId}
        """.executeUpdate()
    }

  def upsertWithGoogleData(userInfo: GoogleUserInfo): User = {
    val userRow = getWithGoogleId(userInfo.id) match {
      case Some(userRow) =>
        userRow.copy(
          user = userRow.user
            .copy(name = userInfo.name, email = userInfo.email, picture = userInfo.picture)
        )
      case None =>
        val id = UUID.randomUUID().toString
        UserRow(User(id, userInfo.name, userInfo.email, userInfo.picture), Some(userInfo.id))
    }
    save(userRow)
    userRow.user
  }

  def delete(id: String): Unit =
    db.withConnection { implicit con =>
      SQL"""DELETE FROM users
            WHERE id = $id"""
        .executeUpdate()
    }
}
