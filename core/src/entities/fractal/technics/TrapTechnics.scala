package entities.fractal.technics

import entities.fractal.sequence.{HasSequenceConstructor, Sequence2}

trait TrapTechnics[A <: Sequence2[Double, Double]] {
  _: HasSequenceConstructor[A] =>

  import Math.sqrt

  @inline private def q(@inline x: Double): Double = x * x

  case class OrbitPoint(maxIteration: Int, trapx: Double, trapy: Double) extends Fractal {
    override def apply(x0: Double, y0: Double): Double =
      sequence(x0, y0, maxIteration).foldLeft(1e20) {
        (d, x, y) =>
          val (dx, dy) = (x - trapx, y - trapy)
          math.min(d, dx * dx + dy * dy)
      }
  }

  case class OrbitImgAxis(maxIteration: Int) extends Fractal {
    override def apply(x0: Double, y0: Double): Double =
      sequence(x0, y0, maxIteration).foldLeftY(1e20) {
        (d, y) => math.min(d, y.abs)
      }
  }

  case class OrbitRealAxis(maxIteration: Int) extends Fractal {
    override def apply(x0: Double, y0: Double): Double =
      sequence(x0, y0, maxIteration).foldLeftX(1e20) {
        (d, x) => math.min(d, x.abs)
      }
  }

  def CircleP2(maxIteration: Int) = CircleTrap(maxIteration, -1, 0, 0.25)

  case class CircleTrap(maxIteration: Int, cx: Double, cy: Double, cr: Double) extends Fractal {
    def apply(x0: Double, y0: Double): Double =
      sequence(x0, y0, maxIteration).foldLeft(1e20) {
        (v, x, y) => v.min((sqrt(q(x - cx) + q(y - cy)) - cr).abs)
      }
  }

}
