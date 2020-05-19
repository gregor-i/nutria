package repo

import anorm.{RowParser, SqlParser, _}
import io.circe.{Decoder, Encoder}
import io.circe.parser._
import io.circe.syntax._
import nutria.api.{Entity, WithId}
import play.api.db.Database

abstract class EntityRepo[E <: Entity[_]: Decoder: Encoder](tableName: String, rowEntity: String, db: Database) {
  private val rowParser: RowParser[WithId[Option[E]]] = for {
    id           <- SqlParser.str("id")
    owner        <- SqlParser.str("owner")
    maybeFractal <- SqlParser.str(rowEntity).map(data => decode[E](data).toOption)
  } yield WithId[Option[E]](id, owner, maybeFractal)

  val rowToEntity: PartialFunction[WithId[Option[E]], WithId[E]] = {
    case WithId(id, owner, Some(entity)) => WithId(id, owner, entity)
  }

  def list(): List[WithId[Option[E]]] =
    db.withConnection { implicit con =>
      SQL"""SELECT *
            FROM #${tableName}"""
        .as(rowParser.*)
    }

  def get(id: String): Option[WithId[Option[E]]] =
    db.withConnection { implicit con =>
      SQL"""SELECT *
            FROM #${tableName}
            WHERE id = $id"""
        .as(rowParser.singleOpt)
    }

  def listPublic(): List[WithId[Option[E]]] =
    db.withConnection { implicit con =>
      SQL"""SELECT *
            FROM #${tableName}
            WHERE published = true"""
        .as(rowParser.*)
    }

  def listByUser(userId: String): List[WithId[Option[E]]] =
    db.withConnection { implicit con =>
      SQL"""SELECT *
            FROM #${tableName}
            WHERE owner = ${userId}"""
        .as(rowParser.*)
    }

  def save(withId: WithId[E]): Unit =
    save(withId.id, withId.owner, withId.entity)

  def save(id: String, owner: String, entity: E): Unit =
    db.withConnection { implicit con =>
      val data = entity.asJson.noSpaces
      SQL"""INSERT INTO #${tableName} (id, owner, published, #${rowEntity})
            VALUES (${id}, ${owner}, ${entity.published}, $data)
            ON CONFLICT (id) DO UPDATE
            SET #${rowEntity} = $data,
                published = ${entity.published}
        """.executeUpdate()
    }

  def delete(id: String): Unit =
    db.withConnection { implicit con =>
      SQL"""DELETE FROM #${tableName}
            WHERE id = $id"""
        .executeUpdate()
    }
}
