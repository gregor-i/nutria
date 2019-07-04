package nutria.data.sequences

import nutria.core.Point
import nutria.data.DoubleSequence
import spire.implicits._
import spire.math.Complex

trait Newton {

  def c0(lambda:Complex[Double]): Complex[Double]
  def f(c:Complex[Double], lambda:Complex[Double]):Complex[Double]
  def f_der(c:Complex[Double], lambda:Complex[Double]):Complex[Double]

  def iteration(c:Complex[Double], lambda:Complex[Double]):Complex[Double] = c - f(c, lambda) / f_der(c, lambda)

  final class Sequence(x0: Double, y0: Double, val maxIterations: Int, val threshold:Double) extends DoubleSequence {
    val lambda = Complex(x0, y0)
    private[this] var c = c0(lambda)
    private[this] var lastC = c
    private[this] var i = 0

    def state = c.asTuple
    def publicIteration = i

    def hasNext: Boolean = f(c, lambda).abs > threshold && i < maxIterations

    def next(): (Double, Double) = {
      lastC = c
      c = iteration(c, lambda)
      i += 1
      (c.real, c.imag)
    }

    def partOfLastIteration:Double = {
      val logT = Math.log(threshold)
      val logD0 = Math.log(f(lastC, lambda).abs)
      val logD1 = Math.log(f(c, lambda).abs)
      (logT - logD0) / (logD1 - logD0)
    }
  }

  def apply(maxIterations:Int, threshold:Double = 1e-8):Point => Sequence = p => new Sequence(p._1, p._2, maxIterations, threshold)
}

