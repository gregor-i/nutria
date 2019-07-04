package nutria.data

import java.io.File

import nutria.core.Dimensions
import nutria.data.image.SaveFolder

trait Defaults
  extends DefaultColor
    with DefaultDimensions
    with nutria.core.DefaultViewport
    with DefaultSaveFolder

trait DefaultColor {
  val defaultColor: Color[Double] = colors.Wikipedia
}

trait DefaultSaveFolder {
  val defaultSaveFolder: SaveFolder = SaveFolder("." + File.separator)
}

trait DefaultDimensions {
  val fujitsu = new Dimensions(1920, 1200)
  val fullHD = new Dimensions(1920, 1080)
  val lenovoX1 = new Dimensions(2560, 1440)

  val defaultDimensions: Dimensions = fullHD
}

object Defaults extends Defaults
