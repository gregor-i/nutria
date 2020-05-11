package nutria.staticRenderer

import nutria.core.viewport.{Dimensions, Viewport}
import nutria.core.{Examples, FractalImage, refineUnsafe}

class RendererSpec extends RenderingSuite {
  renderingTest("renders high resolution Mandelbrot images")(
    fractal = FractalImage(Examples.timeEscape, Viewport.mandelbrot),
    dimensions = Dimensions.fullHD.scale(4),
    fileName = s"${baseFolder}/Mandelbrot-high-resolution.png"
  )

  renderingTest("renders a Mandelbrot with high anti aliase")(
    fractal = FractalImage(Examples.timeEscape, Viewport.mandelbrot, antiAliase = refineUnsafe(10)),
    dimensions = Dimensions.fullHD,
    fileName = s"${baseFolder}/Mandelbrot-high-anti-aliase.png"
  )

  renderingTest("renders Newton Iterations")(
    fractal = FractalImage(Examples.timeEscape, Viewport.mandelbrot),
    dimensions = Dimensions.fullHD,
    fileName = s"${baseFolder}/NewtonIteration-default.png"
  )
}
