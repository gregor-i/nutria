package nutria.core.color

import nutria.core.Color

case class Periodic(color: Color[Double], offset: Double, repeat: Int) extends Color[Double] {
  override def apply(v: Double): RGB = color((v * repeat + offset) % 1)
}
