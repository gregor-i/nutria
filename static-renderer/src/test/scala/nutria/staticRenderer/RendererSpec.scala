package nutria.staticRenderer

import eu.timepit.refined.api.Refined
import eu.timepit.refined.numeric.Positive
import eu.timepit.refined.refineMV
import io.circe.parser
import nutria.core.{DivergingSeries, FractalEntity, FractalImage}
import nutria.core.viewport.Dimensions
import nutria.macros.StaticContent
import org.scalatest.funsuite.AnyFunSuite

import scala.util.chaining._

class RendererSpec extends AnyFunSuite {
  val baseFolder = s"./temp/${getClass.getSimpleName}"

  test("renders high resolution Mandelbrot images") {
    Renderer.renderToFile(
      fractalImage = FractalImage(
        DivergingSeries.default
      ),
      dimensions = Dimensions.fullHD.scale(4),
      fileName = s"${baseFolder}/high-resolution-mandelbrot.png"
    )
  }

  test("renders a Mandelbrot with high anti aliase") {
    Renderer.renderToFile(
      fractalImage = FractalImage(
        program = DivergingSeries.default,
        antiAliase = refineMV(10)
      ),
      dimensions = Dimensions.fullHD,
      fileName = s"${baseFolder}/high-anti-aliase-mandelbrot.png"
    )
  }

  test("renders all system fractals") {
    val aa: Int Refined Positive = refineMV(1)
    val systemFractals: Seq[FractalEntity] =
      StaticContent("./backend/conf/systemfractals.json")
        .pipe(parser.parse)
        .flatMap(_.as[Seq[FractalEntity]])
        .getOrElse(throw new IllegalArgumentException("System fractals not readable"))

    for {
      fractal <- systemFractals
      view    <- fractal.views.value
      file = s"${baseFolder}/${fractal.hashCode()}/${view.hashCode()}.png"
    } Renderer.renderToFile(FractalImage(fractal.program, view, aa), Dimensions.thumbnailDimensions, file)
  }

}
