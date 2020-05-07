package nutria.core.viewport

import monocle.macros.Lenses

@Lenses
case class Dimensions(width: Int, height: Int) {
  def scale(factor: Double) = Dimensions((width * factor).toInt, (height * factor).toInt)
}

object Dimensions {
  val thumbnail = Dimensions(400, 225)
  val favicon   = Dimensions(256, 256)
  val fullHD    = Dimensions(1920, 1080)
}
