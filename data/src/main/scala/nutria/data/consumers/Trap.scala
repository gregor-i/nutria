package nutria.data.consumers

import java.lang.Math.sqrt

import nutria.data.sequences.Mandelbrot
import nutria.data.{DoubleSequence, MathUtils}

object Trap extends MathUtils {

  def OrbitPoint(trapx: Double, trapy: Double): DoubleSequence => Double =
    _.foldLeft(Double.MaxValue) {
      (d, t) => d.min(q(t._1 - trapx) + q(t._2 - trapy))
    }

  val OrbitImgAxis: DoubleSequence => Double =
    seq => seq.map(_._2).foldLeft(Double.MaxValue) {
      (d, y) => d.min(y.abs)
    }

  val OrbitRealAxis: DoubleSequence => Double =
    seq => seq.map(_._1).foldLeft(Double.MaxValue) {
      (d, x) => d.min(x.abs)
    }

  val OrbitBothAxis: DoubleSequence => Double =
    seq => seq.foldLeft(Double.MaxValue) {
      (d, t) => d.min(t._1.abs.min(t._2.abs))
    }

  def CircleTrap(cx: Double, cy: Double, cr: Double): DoubleSequence => Double = {
    @inline def d(x: Double, y: Double) = (sqrt(q(x - cx) + q(y - cy)) - cr).abs

    _.foldLeft(Double.MaxValue) {
      (v, t) => v.min(d(t._1, t._2))
    }
  }

  val CircleP2: Mandelbrot.Sequence => Double = CircleTrap(-1, 0, 0.25)

  object GaussianIntegerTraps {
    @inline def distance(x: Double, y: Double): Double =
      q(x - Math.round(x)) + q(y - Math.round(y))

    def apply[A <: DoubleSequence](): A => Double = {
      _.foldLeft(Double.MaxValue) {
        (v, t) => v.min(distance(t._1, t._2))
      }
    }
  }
}
