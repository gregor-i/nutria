package entities
package viewport

case class Transform(view: Viewport, dimensions: Dimensions) extends HasDimensions with ((Double,Double) => (Double, Double)) {
  val scaleX = view.A.x / width
  val scaleY = view.B.y / height
  val shearX = view.B.x / height
  val shearY = view.A.y / width
  val translationX = view.origin.x
  val transaletionY = view.origin.y

  def transformX(x: Double, y: Double): Double = scaleX * x + shearX * y + translationX
  def transformY(x: Double, y: Double): Double = shearY * x + scaleY * y + transaletionY
  def transform(x: Double, y: Double): (Double, Double) = (transformX(x, y), transformY(x,y))
  override def apply(x: Double, y: Double) = transform(x, y)

  // for multi-sampling. 
  def transformX(x0: Int, y0: Int, xi: Int, yi: Int, multi: Double): Double = transformX(x0 + xi * multi, y0 + yi * multi)
  def transformY(x0: Int, y0: Int, xi: Int, yi: Int, multi: Double): Double = transformY(x0 + xi * multi, y0 + yi * multi)

  // def transformX(x0: Int, y0: Int, xo: Double, yo: Double): Double = transformX(x0 + xo, y0 + yo)
  // def transformY(x0: Int, y0: Int, xo: Double, yo: Double): Double = transformY(x0 + xo, y0 + yo)

  def invertX(x: Double, y: Double): Double =
    (scaleY * (x - translationX) - shearX * (y - transaletionY)) / (scaleX * scaleY - shearX * shearY)
  def invertY(x: Double, y: Double): Double =
    (scaleX * (y - transaletionY) - shearY * (x - translationX)) / (scaleX * scaleY - shearX * shearY)
  def invert(x: Double, y: Double): (Double, Double) = (invertX(x, y), invertY(x,y))
}
