package nutria.data

import java.io.File

import nutria.core.{Color, Dimensions}
import nutria.core.image.SaveFolder
import nutria.core.viewport.{Point, Viewport}
import nutria.data.colors.Wikipedia

trait Defaults extends DefaultColor with DefaultDimensions with DefaultViewport with DefaultSaveFolder

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

trait DefaultSaveFolder {
  val defaultSaveFolder:SaveFolder = SaveFolder("." + File.separator)
}

object Defaults extends Defaults
