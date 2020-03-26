package nutria.staticRenderer

import eu.timepit.refined.refineMV
import nutria.core.viewport.{DefaultViewport, Dimensions, Viewport}
import nutria.core.{DivergingSeries, FractalImage, OuterDistance, RGB}

import scala.util.chaining._

object RenderFavicon {
  def main(args: Array[String]): Unit = {
    val program = DivergingSeries.default
      .copy(
        coloring = OuterDistance(
          colorFar = RGB.black.withAlpha(),
          colorNear = RGB.black.withAlpha(0.0),
          colorInside = RGB.black.withAlpha()
        )
      )

    val view = DefaultViewport.defaultViewport
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

    Renderer.renderToFile(image, Dimensions(256, 256), "backend/public/img/icon.png")
  }
}
