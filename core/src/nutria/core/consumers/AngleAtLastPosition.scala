package nutria.core.consumers

import nutria.core.color.{HSV, RGB}
import nutria.core.sequences.DoubleSequence

object AngleAtLastPosition {
  def apply[A <: DoubleSequence](): A => Double =
    seq => {
      while (seq.next()) ()
      val (x, y) = seq.public
      Math.atan2(x, y)
    }
}

object DirectColors {
  def apply[A <: DoubleSequence](): A => RGB =
    seq => {
      var i = 1
      while (seq.next()) i += 1
      val (x, y) = seq.public
      val a = Math.atan2(x, y)

      val H = (a / Math.PI * 360 + 360) % 360
      val S = Math.exp(-i / 20d)
      val V = S

      if(H == H && S == S && V == V)
        HSV.HSV2RGB(H, S, S)
      else
        RGB.black
    }
}