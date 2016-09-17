package entities.fractal.technics

import entities.fractal.sequence.{HasSequenceConstructor, Sequence2}

trait CardioidTechnics[A <: Sequence2[Double, Double]] {
  _: HasSequenceConstructor[A] =>

  import Math.{PI, cos, sin, sqrt}

  @inline private def q(@inline x: Double): Double = x * x

  case class CardioidHeuristic(maxIteration: Int, numberOfPoints: Int) extends Fractal {
    def contour(t: Double) = (0.5 * cos(t) - 0.25 * cos(t * 2), 0.5 * sin(t) - 0.25 * sin(2 * t))

    val points = (0 to numberOfPoints)
      .map(_ * PI / numberOfPoints)
      .map(contour)

    override def apply(x0: Double, y0: Double): Double =
      sequence(x0, y0.abs, maxIteration).foldLeft(1e20) {
        (v, x, y) =>
          points.foldLeft(v) {
            case (d, (px, py)) => d.min(q(px - x) + q(py - y))
          }
      }
  }

  case class CardioidNumeric(maxIteration: Int, newtonIterations: Int) extends Fractal {

    def contour(t: Double) = (0.5 * cos(t) - 0.25 * cos(t * 2), 0.5 * sin(t) - 0.25 * sin(2 * t))

    def dist(t: Double, x: Double, y: Double): Double = {
      val (cx, cy) = contour(t)
      Math.sqrt(q(cx - x) + q(cy - y))
    }

    // sqrt( (cos(t)/2 - cos(2t)/4-x)^2 + (sin(t)/2 - sin(2t)/4-y)^2 )
    // d/dt sqrt( (cos(t)/2 - cos(2t)/4-x)^2 + (sin(t)/2 - sin(2t)/4-y)^2 )
    // => (2 (Cos[t]/2 - Cos[2 t]/2) (-y + Sin[t]/2 - Sin[2 t]/4) + 2 (-x + Cos[t]/2 - Cos[2 t]/4) (-Sin[t]/2 + Sin[2 t]/2))/(2 Sqrt[(-x + Cos[t]/2 - Cos[2 t]/4)^2 + (-y + Sin[t]/2 - Sin[2 t]/4)^2])

    // d/dt d/dt sqrt( (cos(t)/2 - cos(2t)/4-x)^2 + (sin(t)/2 - sin(2t)/4-y)^2 )
    // => (2*(c2-c1/2)*(c1/2-c2/4-x)+2*(s2-s1/2)*(s1/2-s2/4-y)+2*q(s2/2-s1/2)+2*q(c1/2-c2/2))/(2*sqrt(q(c1/2-c2/4-x)+q(s1/2-s2/4-y)))-q(2*(s2/2-s1/2)*(c1/2-c2/4-x)+2*(c1/2-c2/2)*(s1/2-s2/4-y))/(4*Math.pow(q(c1/2-c2/4-x)+q(s1/2-s2/4-y), 1.5))

    def d_derived(t: Double, x: Double, y: Double): Double = {
      val h = 0.0001
      (dist(t + h, x, y) - dist(t, x, y)) / h
    }

    def d_derived2(t: Double, x: Double, y: Double): Double = {
      val h = 0.0001
      (dist(t + 2 * h, x, y) - 2 * dist(t + h, x, y) + dist(t, x, y)) / (h * h)
    }

    def d_derived_ana(t: Double, x: Double, y: Double): Double = {
      val c1 = cos(t)
      val c2 = cos(t * 2)
      val s1 = sin(t)
      val s2 = sin(t * 2)
      (2 * (cos(t) / 2 - cos(2 * t) / 2) * (-y + sin(t) / 2 - sin(2 * t) / 4) + 2 * (-x + cos(t) / 2 - cos(2 * t) / 4) * (-sin(t) / 2 + sin(2 * t) / 2)) / (2 * sqrt(q(-x + cos(t) / 2 - cos(2 * t) / 4) + q(-y + sin(t) / 2 - sin(2 * t) / 4)))
    }

    def d_derived2_ana(t: Double, x: Double, y: Double): Double = {
      val c1 = cos(t)
      val c2 = cos(t * 2)
      val s1 = sin(t)
      val s2 = sin(t * 2)

      ((c2 - c1 / 2) * (c1 / 2 - c2 / 4 - x) + (s2 - s1 / 2) * (s1 / 2 - s2 / 4 - y) + q(s2 / 2 - s1 / 2) + q(c1 / 2 - c2 / 2)) / sqrt(q(c1 / 2 - c2 / 4 - x) + q(s1 / 2 - s2 / 4 - y)) -
        q((s2 - s1) * (c1 / 2 - c2 / 4 - x) + (c1 - c2) * (s1 / 2 - s2 / 4 - y)) / (4 * Math.pow(q(c1 / 2 - c2 / 4 - x) + q(s1 / 2 - s2 / 4 - y), 1.5))
    }

    // calculates [d/dt dist(t, x, y)] / [d/dt^2 dist(t, x, y)]
    def der1DivDer2(t: Double, x: Double, y: Double) = {
      val (c1, c2) = (cos(t), cos(t * 2))
      val (c1h, c2h, c2hh) = (c1 / 2, c2 / 2, c2 / 4)

      val (s1, s2) = (sin(t), sin(t * 2))
      val (s1h, s2h, s2hh) = (s1 / 2, s2 / 2, s2 / 4)

      val (cd, sd) = (c1h - c2h, s2h - s1h)
      val (ct, st) = (c1h - c2hh - x, s1h - s2hh - y)

      val d1 = cd * st + ct * sd
      val d2 = (c2 - c1h) * ct + (s2 - s1h) * st + q(sd) + q(cd) - q(d1) / (q(ct) + q(st))
      d1 / d2
    }

    def newton(t0: Double, x: Double, y: Double): Double = {
      var t = t0
      for (i <- 0 until newtonIterations)
        t -= der1DivDer2(t, x, y)
      t
    }


    val phi = (Math.sqrt(5) + 1) / 2

    def golden(x: Double, _y: Double): Double = {
      val y = _y.abs
      var a = 0.0
      var b = Math.PI
      for (i <- 0 until newtonIterations) {
        val c = b - (b - a) / phi
        val d = a + (b - a) / phi
        if (dist(c, x, y) < dist(d, x, y))
          b = d
        else
          a = c
      }
      (a + b) / 2
    }

    def minimalDistance(x: Double, _y: Double): Double = {
      val y = _y.abs
      val t0 = golden(x, y)
      val n = newton(t0, x, y)
      dist(t0, x, y)
    }

    override def apply(x0: Double, y0: Double): Double =
      sequence(x0, y0, maxIteration).foldLeft(1e20) {
        (v, x, y) => v.min(minimalDistance(x, y))
      }
  }

}
