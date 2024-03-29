package nutria.staticRenderer

import nutria.core._

import scala.util.Try
import scala.util.chaining._

object RenderAssets {
  def main(args: Array[String]): Unit =
    for {
      _ <- favicon()
      _ <- example_DivergingSeries()
      _ <- example_NewtonIteration()
    } yield ()

  private def imgFolder(src: String): String =
    s"backend/public/img/$src"

  private def favicon(): Try[Unit] = {
    val program = Examples.outerDistance
      .pipe(
        FractalTemplate.applyParameters(_)(
          Vector(
            RGBAParameter("colorFar", value = RGB.black.withAlpha(0.0)),
            RGBAParameter("colorNear", value = RGB.black.withAlpha()),
            RGBAParameter("colorInside", value = RGB.black.withAlpha())
          )
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
      template = program,
      viewport = view,
      antiAliase = 4
    )

    Renderer.renderToFile(image, Dimensions.favicon, imgFolder("icon.png"))
  }

  private def example_DivergingSeries(): Try[Unit] = Renderer.renderToFile(
    fractalImage = FractalImage(
      template = Examples.timeEscape,
      viewport = Viewport.mandelbrot,
      antiAliase = 4
    ),
    dimensions = Dimensions.thumbnail,
    fileName = imgFolder("example_DivergingSeries.png")
  )

  private def example_NewtonIteration(): Try[Unit] =
    Renderer.renderToFile(
      fractalImage = FractalImage(
        template = Examples.newtonIteration,
        viewport = Viewport.aroundZero,
        antiAliase = 4
      ),
      dimensions = Dimensions.thumbnail,
      fileName = imgFolder("example_NewtonIteration.png")
    )
}
