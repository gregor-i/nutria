package module

import io.circe.parser
import javax.inject.Singleton
import nutria.core.FractalEntity

import scala.io.Source


@Singleton
class SystemFractals {
  val systemFractals =
    parser.parse {
      Source.fromResource("systemfractals.json")
        .getLines()
        .mkString("\n")
    }.flatMap(_.as[Vector[FractalEntity]]) match {
      case Right(x) => x
      case Left(error) => throw error
    }
}
