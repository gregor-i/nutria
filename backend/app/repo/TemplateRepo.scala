package repo

import javax.inject.{Inject, Singleton}
import nutria.api.FractalTemplateEntity
import play.api.db.Database

@Singleton()
class TemplateRepo @Inject() (db: Database) extends EntityRepo[FractalTemplateEntity]("templates", "template", db)
