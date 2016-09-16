package entities.color

case class Invert(color: Color) extends Color {
  override def apply(d: Double): Int = color.apply(1 - d)
}
