package nutria.staticRenderer

import nutria.core.{DivergingSeries, FractalImage, NewtonIteration, NormalMap, OuterDistance, TimeEscape, refineUnsafe}
import nutria.core.viewport.Dimensions
import org.scalatest.funsuite.AnyFunSuite

class RendererSpec extends AnyFunSuite with RenderingSuite {
  renderingTest("renders high resolution Mandelbrot images")(
    fractal = FractalImage(
      DivergingSeries.default
    ),
    dimensions = Dimensions.fullHD.scale(4),
    fileName = s"${baseFolder}/Mandelbrot-high-resolution.png"
  )

  renderingTest("renders a Mandelbrot with high anti aliase")(
    fractal = FractalImage(
      program = DivergingSeries.default,
      antiAliase = refineUnsafe(10)
    ),
    dimensions = Dimensions.fullHD,
    fileName = s"${baseFolder}/Mandelbrot-high-anti-aliase.png"
  )

  renderingTest("renders Newton Iterations")(
    fractal = FractalImage(
      NewtonIteration.default
    ),
    dimensions = Dimensions.fullHD,
    fileName = s"${baseFolder}/NewtonIteration-default.png"
  )

  for {
    coloring <- Seq(TimeEscape(), OuterDistance(), NormalMap())
    name = coloring.getClass.getSimpleName
  } renderingTest(s"renders Mandelbrot with $name")(
    fractal = FractalImage(
      program = DivergingSeries.default.copy(coloring = coloring)
    ),
    fileName = s"${baseFolder}/Mandelbrot-$name.png"
  )
}
