package repo

import anorm.{RowParser, SqlParser, _}
import io.circe.parser._
import io.circe.syntax._
import javax.inject.{Inject, Singleton}
import nutria.core.FractalEntity
import play.api.db.Database
import nutria.core.FractalEntityWithId

case class FractalRow(id: String, owner: String, published: Boolean, maybeFractal: Option[FractalEntity])

@Singleton()
class FractalRepo @Inject()(db: Database) {
  private val rowParser: RowParser[FractalRow] = for {
    id <- SqlParser.str("id")
    owner <- SqlParser.str("owner")
    published <- SqlParser.bool("published")
    maybeFractal <- SqlParser.str("fractal").map(data => decode[FractalEntity](data).toOption)
  } yield FractalRow(id, owner, published, maybeFractal)

  val fractalRowToFractalEntity: PartialFunction[FractalRow, FractalEntityWithId] = {
    case FractalRow(id, owner, published, Some(entity)) =>
      FractalEntityWithId(id, owner, entity.copy(published = published))
  }

  def list(): List[FractalRow] =
    db.withConnection { implicit con =>
      SQL"""SELECT *
            FROM fractals"""
        .as(rowParser.*)
    }

  def get(id: String): Option[FractalRow] =
    db.withConnection { implicit con =>
      SQL"""SELECT *
            FROM fractals
            WHERE id = $id"""
        .as(rowParser.singleOpt)
    }

  def listPublic(): List[FractalRow] =
    db.withConnection { implicit con =>
      SQL"""SELECT *
            FROM fractals
            WHERE published = true"""
        .as(rowParser.*)
    }

  def listByUser(userId: String): List[FractalRow] =
    db.withConnection { implicit con =>
      SQL"""SELECT *
            FROM fractals
            WHERE owner = ${userId}"""
        .as(rowParser.*)
    }

  def save(id: String, owner: String, fractal: FractalEntity): Unit =
    db.withConnection { implicit con =>
      val data = fractal.asJson.noSpaces
      SQL"""INSERT INTO fractals (id, owner, published, fractal)
            VALUES (${id}, ${owner}, ${fractal.published}, $data)
            ON CONFLICT (id) DO UPDATE
            SET fractal = $data,
                published = ${fractal.published}
        """.executeUpdate()
    }

  def delete(userId: String, fractalId: String): Unit =
  db.withConnection { implicit con =>
    SQL"""DELETE FROM fractals
            WHERE id = $fractalId AND owner = $userId"""
      .executeUpdate()
  }

  def delete(fractalId: String): Unit =
    db.withConnection { implicit con =>
      SQL"""DELETE FROM fractals
            WHERE id = $fractalId"""
        .executeUpdate()
    }
}

