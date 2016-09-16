package entities.viewport

case class Dimensions(width: Int, height: Int) {
  def scale(factor: Double) = Dimensions((width * factor).toInt, (height * factor).toInt)
}

object Dimensions {
  val screenHD = Dimensions(1920, 1200)
  val fullHD = Dimensions(1920, 1080)
  val lenovoX1 = Dimensions(2560, 1440)
}

trait HasDimensions {
  def dimensions: Dimensions
  def width = dimensions.width
  def height = dimensions.height
}
