package repo

import anorm.{RowParser, SqlParser, _}
import io.circe.parser._
import io.circe.syntax._
import javax.inject.{Inject, Singleton}
import nutria.api.{FractalTemplateEntity, FractalTemplateEntityWithId, WithId}
import nutria.core.FractalEntity
import play.api.db.Database

// aka: WithId[Option[FractalTemplateEntity]]
case class TemplateRow(
    id: String,
    owner: String,
    published: Boolean,
    maybeTemplate: Option[FractalTemplateEntity]
)

@Singleton()
class TemplateRepository @Inject() (db: Database) {
  private val rowParser: RowParser[TemplateRow] = for {
    id            <- SqlParser.str("id")
    owner         <- SqlParser.str("owner")
    published     <- SqlParser.bool("published")
    maybeTemplate <- SqlParser.str("template").map(data => decode[FractalTemplateEntity](data).toOption)
  } yield TemplateRow(id, owner, published, maybeTemplate.map(_.copy(published = published)))

  val fractalRowToTemplateEntity: PartialFunction[TemplateRow, FractalTemplateEntityWithId] = {
    case TemplateRow(id, owner, published, Some(entity)) =>
      WithId(id, owner, entity.copy(published = published))
  }

  def list(): List[TemplateRow] =
    db.withConnection { implicit con =>
      SQL"""SELECT *
            FROM templates"""
        .as(rowParser.*)
    }

  def get(id: String): Option[TemplateRow] =
    db.withConnection { implicit con =>
      SQL"""SELECT *
            FROM templates
            WHERE id = $id"""
        .as(rowParser.singleOpt)
    }

  def save(id: String, owner: String, template: FractalTemplateEntity): Unit =
    db.withConnection { implicit con =>
      val data = template.asJson.noSpaces
      SQL"""INSERT INTO templates (id, owner, published, template)
            VALUES (${id}, ${owner}, ${template.published}, $data)
            ON CONFLICT (id) DO UPDATE
            SET template = $data,
                published = ${template.published}
        """.executeUpdate()
    }

  def delete(templateId: String): Unit =
    db.withConnection { implicit con =>
      SQL"""DELETE FROM templates
            WHERE id = $templateId"""
        .executeUpdate()
    }
}
