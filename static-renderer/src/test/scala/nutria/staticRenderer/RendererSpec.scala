package nutria.staticRenderer

import nutria.core.{DivergingSeries, FractalImage, NewtonIteration, NormalMap, OuterDistance, TimeEscape, ToFreestyle, refineUnsafe}
import nutria.core.viewport.Dimensions
import org.scalatest.funsuite.AnyFunSuite

import scala.util.chaining._

class RendererSpec extends RenderingSuite {
  renderingTest("renders high resolution Mandelbrot images")(
    fractal = FractalImage(
      DivergingSeries.default.pipe(ToFreestyle.apply)
    ),
    dimensions = Dimensions.fullHD.scale(4),
    fileName = s"${baseFolder}/Mandelbrot-high-resolution.png"
  )

  renderingTest("renders a Mandelbrot with high anti aliase")(
    fractal = FractalImage(
      program = DivergingSeries.default.pipe(ToFreestyle.apply),
      antiAliase = refineUnsafe(10)
    ),
    dimensions = Dimensions.fullHD,
    fileName = s"${baseFolder}/Mandelbrot-high-anti-aliase.png"
  )

  renderingTest("renders Newton Iterations")(
    fractal = FractalImage(
      NewtonIteration.default.pipe(ToFreestyle.apply)
    ),
    dimensions = Dimensions.fullHD,
    fileName = s"${baseFolder}/NewtonIteration-default.png"
  )

  for {
    coloring <- Seq(TimeEscape(), OuterDistance(), NormalMap())
    name = coloring.getClass.getSimpleName
  } renderingTest(s"renders Mandelbrot with $name")(
    fractal = FractalImage(
      program = DivergingSeries.default.copy(coloring = coloring).pipe(ToFreestyle.apply)
    ),
    fileName = s"${baseFolder}/Mandelbrot-$name.png"
  )
}
