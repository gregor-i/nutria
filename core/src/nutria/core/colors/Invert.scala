package nutria.core.colors

import nutria.core.Color

object Invert {
  def apply(color: Color[Double]): Color[Double] = color match {
    case Invert(innerColor) => innerColor
    case _ => new Invert(color)
  }
}

case class Invert(color: Color[Double]) extends Color[Double] {
  override def apply(d: Double): RGB = color(1 - d)
}
