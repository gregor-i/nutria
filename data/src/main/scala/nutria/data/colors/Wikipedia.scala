package nutria.data.colors

import nutria.data.Color

object Wikipedia extends Color[Double] {
  val values = List(
    0.0 -> RGB(0, 7, 100),
    0.16 -> RGB(32, 107, 203),
    0.42 -> RGB(237, 255, 255),
    0.6425 -> RGB(255, 170, 0),
    0.8575 -> RGB(0, 2, 0),
    1.0 -> RGB(0, 7, 100)
  )

  @inline private def clamp(x: Double, min: Double, max: Double): Double =
    if (x == x)
      x.max(min).min(max)
    else
      min

  override def apply(_key: Double): RGB = {
    val key = clamp(_key, 0d, 1d)
    if (key == 0.0)
      values.head._2
    else if (key == 1.0)
      values.last._2
    else {
      assert(key > 0.0 && key < 1)
      val i = values.indexWhere(_._1 > key)
      assert(i != -1)
      assert(i != 0)
      val (keyLeft, colorLeft) = values(i - 1)
      val (keyRight, colorRight) = values(i)

      assert(keyLeft <= key)
      assert(keyRight >= key)

      RGB.interpolate(colorLeft, colorRight, (keyLeft - key) / (keyLeft - keyRight))
    }
  }
}
