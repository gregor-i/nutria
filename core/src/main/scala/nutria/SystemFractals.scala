package nutria

import nutria.core.FractalEntity
import nutria.macros.StaticContent

import scala.util.chaining._

object SystemFractals {
  val systemFractals: Vector[FractalEntity] =
    StaticContent("./core/src/main/data/systemfractals.json")
      .pipe(io.circe.parser.parse)
      .flatMap(_.as[Vector[FractalEntity]])
      .fold(error => throw error, right => right)
}
