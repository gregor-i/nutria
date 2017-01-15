package nurtia.data

import java.io.File

import nurtia.data.fractalFamilies.MandelbrotData
import nutria.core.{Color, Dimensions, Viewport}
import nutria.core.colors.Wikipedia
import nutria.core.image.SaveFolder

trait Defaults extends DefaultColor with DefaultDimensions with DefaultViewport with DefaultSaveFolder {
  def default[A](implicit a: A): A = a // just an alias for implicitly
}

trait DefaultColor {
  implicit val defaultColor : Color[Double] = Wikipedia
}

trait DefaultDimensions {
  implicit val defaultDimensions: Dimensions = DimensionInstances.fullHD
}

trait DefaultViewport {
  implicit val defaultViewport: Viewport = MandelbrotData.initialViewport
}

trait DefaultSaveFolder {
  val defaultSaveFolder:SaveFolder = SaveFolder(nutria.core.BuildInfo.defaultSaveFolder.getOrElse("." + File.separator))
}