package entities
package fractal.technics

import entities.fractal.sequence.{HasSequenceConstructor, Sequence2}

trait TrapTechnics[A <: Sequence2[Double, Double]] {
  _: HasSequenceConstructor[A] =>

  import Math.sqrt

  @inline private def q(@inline x: Double): Double = x * x

  def OrbitPoint(maxIteration: Int, trapx: Double, trapy: Double): Fractal =
    (x0, y0) =>
      sequence(x0, y0, maxIteration).foldLeft(1e20) {
        (d, x, y) => d.min(q(x - trapx) + q(y - trapy))
      }

  def OrbitImgAxis(maxIteration: Int): Fractal =
    (x0, y0) =>
      sequence(x0, y0, maxIteration).foldLeftY(1e20) {
        (d, y) => d.min(y.abs)
      }

  def OrbitRealAxis(maxIteration: Int): Fractal =
    (x0, y0) =>
      sequence(x0, y0, maxIteration).foldLeftX(1e20) {
        (d, x) => d.min(x.abs)
      }

  def CircleP2(maxIteration: Int) = CircleTrap(maxIteration, -1, 0, 0.25)

  def CircleTrap(maxIteration: Int, cx: Double, cy: Double, cr: Double): Fractal =
    (x0, y0) =>
      sequence(x0, y0, maxIteration).foldLeft(1e20) {
        (v, x, y) => v.min((sqrt(q(x - cx) + q(y - cy)) - cr).abs)
      }

}
