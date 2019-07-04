package nutria.data.colors

object MonoColor {
  abstract class HSVMonoColor(H: Double) extends HSV[Double] {
    def H(lambda: Double) = H
    def S(lambda: Double) = if (lambda < 0.5) 1 else 2 * (1 - lambda)
    def V(lambda: Double) = if (lambda < 0.5) 2 * lambda else 1
  }

  case object Red extends HSVMonoColor(0)
  case object Yellow extends HSVMonoColor(60)
  case object Green extends HSVMonoColor(120)
  case object Cyan extends HSVMonoColor(180)
  case object Blue extends HSVMonoColor(240)
  case object Magenta extends HSVMonoColor(300)
}
