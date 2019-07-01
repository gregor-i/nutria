package nutria.data

import nutria.core.{Color, Dimensions}
import nutria.core.viewport.{Point, Viewport}
import nutria.data.colors.Wikipedia

trait Defaults extends DefaultColor with DefaultDimensions with DefaultViewport

trait DefaultColor {
  val defaultColor : Color[Double] = Wikipedia
}

trait DefaultDimensions {
  val fujitsu = new Dimensions(1920, 1200)
  val fullHD = new Dimensions(1920, 1080)
  val lenovoX1 = new Dimensions(2560, 1440)

  val defaultDimensions: Dimensions = fullHD
}

trait DefaultViewport {
  val defaultViewport: Viewport = Viewport(Point(-2.5, -1), Point(3.5, 0), Point(0, 2))
}

object Defaults extends Defaults
