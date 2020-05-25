package repo

import javax.inject.{Inject, Singleton}
import nutria.api.{FractalEntity, FractalImageEntity}
import play.api.db.Database

@Singleton()
class ImageRepo @Inject() (db: Database) extends EntityRepo[FractalImageEntity]("images", "image", db)
