package nutria.staticRenderer

import io.circe.{Json, parser}
import nutria.core.viewport.Dimensions
import nutria.core.{FractalEntity, FractalImage}
import nutria.macros.StaticContent
import org.scalatest.funsuite.AnyFunSuite

import scala.util.chaining._

class SystemFractalSpec extends AnyFunSuite with RenderingSuite {
  val systemFractals: Seq[FractalEntity] =
    StaticContent("./backend/conf/systemfractals.json")
      .pipe(parser.parse)
      .flatMap(_.as[Seq[FractalEntity]])
      .getOrElse(Seq.empty)

  test("all system fractals can be parsed") {
    val seq = StaticContent("./backend/conf/systemfractals.json")
      .pipe(parser.parse)
      .flatMap(_.as[Seq[Json]])
      .toOption
      .get

    for (s <- seq) {
      s.as[FractalEntity] match {
        case Right(_) => ()
        case Left(_)  => fail(s"$s count not be parsed")
      }
    }
    succeed
  }

  for {
    (fractalImage, index) <- FractalImage.allImages(systemFractals).zipWithIndex
  } renderingTest(s"renders all system fractals ($index)")(
    fractal = fractalImage,
    dimensions = Dimensions.thumbnail,
    fileName = s"${baseFolder}/${index}.png"
  )
}
