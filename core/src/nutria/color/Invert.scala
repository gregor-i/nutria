package nutria.color

object Invert {
  def invert(color: Color): Color = color match {
    case Invert(innerColor) => innerColor
    case _ => Invert(color)
  }
}

case class Invert(color: Color) extends Color {
  override def apply(d: Double): Int = color.apply(1 - d)
}
