package nutria.data.consumers

import nutria.data.{DoubleSequence, MathUtils}
import spire.implicits._
import spire.math.Complex

object CountIterations extends MathUtils {
  def apply(): Iterator[_] => Int = _.size

  def double() = apply() andThen (_.toDouble)

  def smoothed(): DoubleSequence => Double =
    seq => {
      val iterations = seq.size
      val state = seq.next
      val smoothing = -Math.log(Math.log(Complex[Double](state._1, state._2).abs)) / Math.log(2)
      iterations + smoothing
    }
}
