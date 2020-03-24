package module

import io.circe
import javax.inject.Singleton
import nutria.core.FractalEntity
import nutria.macros.StaticContent
import scala.util.chaining._

@Singleton
class SystemFractals {
  val systemFractals: Vector[FractalEntity] =
    StaticContent("./backend/conf/systemfractals.json")
      .pipe(circe.parser.parse)
      .flatMap(_.as[Vector[FractalEntity]])
      .fold(error => throw error, right => right)
}
