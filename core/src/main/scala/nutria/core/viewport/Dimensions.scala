package nutria.core.viewport

case class Dimensions(width: Int, height: Int) {
  def scale(factor: Double) = Dimensions((width * factor).toInt, (height * factor).toInt)
}

trait HasDimensions {
  val dimensions: Dimensions
  def width: Int = dimensions.width
  def height: Int = dimensions.height
}
