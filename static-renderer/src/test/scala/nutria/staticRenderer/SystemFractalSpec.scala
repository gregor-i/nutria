package nutria.staticRenderer

import io.circe.parser
import nutria.core.{AntiAliase, FractalEntity, FractalImage, NewtonIteration, refineUnsafe}
import nutria.core.viewport.Dimensions
import nutria.macros.StaticContent
import org.scalatest.funsuite.AnyFunSuite

import scala.util.chaining._

class SystemFractalSpec extends AnyFunSuite with RenderingSuite {
  val baseFolder = s"./temp/${getClass.getSimpleName}"

  val aa: AntiAliase = refineUnsafe(1)

  val systemFractals: Seq[FractalEntity] =
    StaticContent("./backend/conf/systemfractals.json")
      .pipe(parser.parse)
      .flatMap(_.as[Seq[FractalEntity]])
      .getOrElse(throw new IllegalArgumentException("System fractals not readable"))

  for {
    (fractalImage, index) <- FractalImage.allImages(systemFractals).zipWithIndex
  } renderingTest(s"renders all system fractals ($index)")(
    fractal = fractalImage,
    dimensions = Dimensions.thumbnail,
    fileName = s"${baseFolder}/${index}.png"
  )
}
