package nutria.core.consumers

import nutria.core.sequences.DoubleSequence

object AngleAtLastPosition {
  def apply[A <: DoubleSequence](): A => Double =
    seq => {
      while (seq.next()) ()
      val (x, y) = seq.public
      Math.atan2(x, y)
    }
}