package repo

import anorm.{RowParser, SqlParser, _}
import io.circe.parser._
import io.circe.syntax._
import javax.inject.{Inject, Singleton}
import nutria.core.FractalEntity
import play.api.db.Database

case class FractalRow(id: String, maybeFractal: Option[FractalEntity])

@Singleton()
class FractalRepo @Inject()(db: Database) {
  private val rowParser: RowParser[FractalRow] = for {
    id <- SqlParser.str("id")
    maybeFractal <- SqlParser.str("fractal").map(data => decode[FractalEntity](data).toOption)
  } yield FractalRow(id, maybeFractal)

  def get(id: String): Option[FractalRow] =
    db.withConnection { implicit con =>
      SQL"""SELECT *
            FROM fractals
            WHERE id = $id"""
        .as(rowParser.singleOpt)
    }

  def list(): List[FractalRow] =
    db.withConnection { implicit con =>
      SQL"""SELECT *
            FROM fractals"""
        .as(rowParser.*)
    }

  def save(row: FractalRow): Unit =
    db.withConnection { implicit con =>
      val data = row.maybeFractal.asJson.noSpaces
      SQL"""INSERT INTO fractals (id, fractal)
            VALUES (${row.id}, $data)
            ON CONFLICT (id) DO UPDATE
            SET fractal = $data
        """.executeUpdate()
    }

  def delete(id: String): Unit =
    db.withConnection { implicit con =>
      SQL"""DELETE FROM fractals
            WHERE id = $id"""
        .executeUpdate()
    }
}

@Singleton()
class CachedFractalRepo @Inject()(repo: FractalRepo) {
  private var cached: List[FractalRow] = null

  def get(id: String): Option[FractalRow] =
    list().find(_.id == id)

  def list(): List[FractalRow] = {
    if (cached == null)
      cached = repo.list()
    cached
  }

  def save(row: FractalRow): Unit = {
    repo.save(row)
    cached = repo.list()
    ()
  }

  def delete(id: String): Unit = {
    repo.delete(id)
    cached = repo.list()
    ()
  }
}