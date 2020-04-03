package nutria.staticRenderer

import eu.timepit.refined.refineMV
import nutria.core.viewport.{Viewport, Dimensions}
import nutria.core.{DivergingSeries, FractalImage, NewtonIteration, OuterDistance, RGB}

import scala.util.chaining._

object RenderAssets {

  def main(args: Array[String]): Unit = {
    favicon()
    examples()
  }

  private def imgFolder(src: String): String =
    s"backend/public/img/$src"

  private def examples(): Unit = {
    val program = DivergingSeries.default
      .copy(
        coloring = OuterDistance(
          colorFar = RGB.black.withAlpha(0.0),
          colorNear = RGB.black.withAlpha(),
          colorInside = RGB.black.withAlpha()
        )
      )

    val view = Viewport.mandelbrot
      .pipe { view =>
        Viewport(view.origin, view.B, view.A).flipB
      }
      .pipe(_.contain(1, 1))
      .pipe(_.rotate(angle = -Math.PI / 4))
      .pipe(_.zoom((0.65, 0.35), 0.60))

    val image = FractalImage(
      program = program,
      view = view,
      antiAliase = refineMV(4)
    )

    Renderer.renderToFile(image, Dimensions.favicon, imgFolder("icon.png"))
  }

  private def favicon(): Unit = {
    Renderer.renderToFile(
      fractalImage = FractalImage(
        program = DivergingSeries.default,
        view = Viewport.mandelbrot,
        antiAliase = refineMV(4)
      ),
      dimensions = Dimensions.thumbnail,
      fileName = imgFolder("example_DivergingSeries.png")
    )

    Renderer.renderToFile(
      fractalImage = FractalImage(
        program = NewtonIteration.default,
        view = Viewport.aroundZero,
        antiAliase = refineMV(4)
      ),
      dimensions = Dimensions.thumbnail,
      fileName = imgFolder("example_NewtonIteration.png")
    )
  }
}
