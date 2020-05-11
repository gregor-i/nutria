package nutria.staticRenderer

import nutria.core._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.chaining._

object RenderAssets {

  def main(args: Array[String]): Unit =
    for {
      _ <- favicon()
      _ <- example_DivergingSeries()
      _ <- example_NewtonIteration()
      _ = println("execution finished")
    } yield ()

  private def imgFolder(src: String): String =
    s"backend/public/img/$src"

  private def favicon(): Future[Unit] = {
    val program = Examples.outerDistance
      .setParameter(RGBAParameter("colorFar", RGB.black.withAlpha(0.0)))
      .setParameter(RGBAParameter("colorNear", RGB.black.withAlpha()))
      .setParameter(RGBAParameter("colorInside", RGB.black.withAlpha()))

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
      antiAliase = refineUnsafe(4)
    )

    Renderer.renderToFile(image, Dimensions.favicon, imgFolder("icon.png"))
  }

  private def example_DivergingSeries(): Future[Unit] = Renderer.renderToFile(
    fractalImage = FractalImage(
      program = Examples.timeEscape,
      view = Viewport.mandelbrot,
      antiAliase = refineUnsafe(4)
    ),
    dimensions = Dimensions.thumbnail,
    fileName = imgFolder("example_DivergingSeries.png")
  )

  private def example_NewtonIteration(): Future[Unit] =
    Renderer.renderToFile(
      fractalImage = FractalImage(
        program = Examples.newtonIteration,
        view = Viewport.aroundZero,
        antiAliase = refineUnsafe(4)
      ),
      dimensions = Dimensions.thumbnail,
      fileName = imgFolder("example_NewtonIteration.png")
    )
}
