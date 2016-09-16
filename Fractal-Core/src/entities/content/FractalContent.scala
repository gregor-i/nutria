package entities
package content

case class FractalContent(fractal: Fractal, transform: Transform) extends Content{
  val dimensions = transform.dimensions
  def apply(x:Int, y:Int): Double =
    fractal(transform.transformX(x, y), transform.transformY(x, y))
}