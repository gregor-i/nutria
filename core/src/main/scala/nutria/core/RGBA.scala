package nutria.core

object RGBA extends CirceCodex {
  def interpolate(la: RGBA, lb: RGBA, p: Double): RGBA = {
    require(0 <= p && p <= 1, s"$p was not in the expected interval [0, 1]")
    val q = 1 - p
    RGBA(la.R * q + lb.R * p, la.G * q + lb.G * p, la.B * q + lb.B * p, la.A * q + lb.A * p)
  }
}

final case class RGBA(R: Double, G: Double, B: Double, A: Double = 1.0) {
  require(
    (0 <= R && R < 256) && (0 <= G && G < 256) && (0 <= B && B < 256) && (0 <= A && A <= 1),
    s"Requirement for RGB failed. input: R=$R, G=$G, B=$B, A=$A"
  )
}
