package nutria.data.colors

import nutria.data.Color

trait HSV[A] extends Color[A] {
  def H(lambda: A): Double
  def S(lambda: A): Double
  def V(lambda: A): Double

  def apply(lambda: A): RGB = HSV.HSV2RGB(H(lambda), S(lambda), V(lambda))
}

object HSV {
  @inline private def clamp(x: Double, min: Double, max: Double): Double =
    if (x == x)
      x.max(min).min(max)
    else
      min

  def HSV2RGB(_H: Double, _S: Double, _V: Double): RGB = {
    val H = clamp(_H, 0, 360)
    val S = clamp(_S, 0, 1)
    val V = clamp(_V, 0, 1)

    val h = (H / 60).toInt
    val f = H / 60.0 - h
    val q = V * (1 - S * f)
    val p = V * (1 - S)
    val t = V * (1 - S * (1 - f))

    h % 6 match {
      case 0 => RGB(V * 255, t * 255, p * 255)
      case 1 => RGB(q * 255, V * 255, p * 255)
      case 2 => RGB(p * 255, V * 255, t * 255)
      case 3 => RGB(p * 255, q * 255, V * 255)
      case 4 => RGB(t * 255, p * 255, V * 255)
      case 5 => RGB(V * 255, p * 255, q * 255)
    }
  }
}


