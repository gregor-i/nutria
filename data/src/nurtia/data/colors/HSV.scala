package nurtia.data.colors

import nutria.core.colors.HSV


case object TestColor extends HSV[Double] {
  def H(lambda: Double) = lambda * 360 + 60
  def S(lambda: Double) = if (lambda < 0.5) 1 else 2 * (1 - lambda)
  def V(lambda: Double) = if (lambda < 0.5) 2 * lambda else 1
}

case object Rainbow extends HSV[Double] {
  def H(lambda: Double) = lambda * 360 + 240
  def S(lambda: Double) = 1
  def V(lambda: Double) = 1
}

object MonoColor {
  abstract class HSVMonoColor(H: Double) extends HSV[Double] {
    def H(lambd: Double) = H
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

object MonoColor2 {
  abstract class HSVMonoColor(H: Double) extends HSV[Double] {
    def H(lambd: Double) = H
    def S(lambda: Double) = 1 - math.sqrt(1 - math.pow(1 - lambda, 2))
    def V(lambda: Double) = math.sqrt(1 - math.pow(1 - lambda, 2))
  }

  case object Red extends HSVMonoColor(0)
  case object Yello extends HSVMonoColor(60)
  case object Green extends HSVMonoColor(120)
  case object Cyan extends HSVMonoColor(180)
  case object Blue extends HSVMonoColor(240)
  case object Magenta extends HSVMonoColor(300)
}