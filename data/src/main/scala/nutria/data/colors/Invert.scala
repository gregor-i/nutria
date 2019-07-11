package nutria.data.colors

import nutria.data.Color

object Invert {
  def apply(color: Color[Double]): Color[Double] = color match {
    case Invert(innerColor) => innerColor
    case _ => new Invert(color)
  }
}

case class Invert(color: Color[Double]) extends Color[Double] {
  override def apply(d: Double): RGBA = color(1 - d)
}
