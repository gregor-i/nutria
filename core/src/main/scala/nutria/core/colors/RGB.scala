package nutria.core.colors

object RGB {
  val white = RGB(1, 1, 1)
  val black = RGB(0, 0, 0)

  def interpolate(la: RGB, lb: RGB, p: Double): RGB = {
    require(0 <= p && p <= 1, s"$p was not in the expected interval [0, 1]")
    val q = 1 - p
    RGB(la.R * q + lb.R * p,
      la.G * q + lb.G * p,
      la.B * q + lb.B * p)
  }
}

final case class RGB(R: Double, G: Double, B: Double) {
  require((0 <= R && R < 256) && (0 <= G && G < 256) && (0 <= B && B < 256), s"Requirement for RGB failed. input: R=$R, G=$G, B=$B")
  val hex: Int = R.toInt << 16 | G.toInt << 8 | B.toInt

  override def toString: String = "#%02x%02x%02x".format(0xff & R.toInt, 0xff & G.toInt, 0xff & B.toInt)
}

