package nutria.frontend.library

import io.circe.{Decoder, parser}
import nutria.core._
import org.scalajs.dom.XMLHttpRequest
import org.scalajs.dom.ext.Ajax

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.control.NonFatal

object LibraryService {
  def loadFractals(): Future[Vector[FractalEntity]] =
    Ajax.get(s"/api/fractals")
      .map(checkAndParse[Vector[FractalEntity]](200))
      .recover {
        case NonFatal(error) =>
          println(error)
          Vector.empty
      }

  private def checkAndParse[A: Decoder](expected: Int)(req: XMLHttpRequest): A =
    parser.parse(req.responseText).flatMap(_.as[A]).right.get
}