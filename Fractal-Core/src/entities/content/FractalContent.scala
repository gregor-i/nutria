package entities
package content

import entities.fractal.technics.Fractal

case class FractalContent(fractal: Fractal, transform: Transform) extends Content{
  val dimensions = transform.dimensions
  def apply(x:Int, y:Int): Double =
    fractal(transform.transformX(x, y), transform.transformY(x, y))
}