package repo

import javax.inject.{Inject, Singleton}
import nutria.core.FractalEntity
import play.api.db.Database

@Singleton()
class FractalRepo @Inject() (db: Database) extends EntityRepo[FractalEntity]("fractals", "fractal", db)
