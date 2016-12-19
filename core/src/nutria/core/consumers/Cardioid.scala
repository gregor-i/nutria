/*
 * Copyright (C) 2016  Gregor Ihmor & Merlin Göttlinger
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package nutria.core.consumers

import nutria.core.MathUtils
import nutria.core.sequences.Mandelbrot

object CardioidHeuristic extends MathUtils{
  import Math.{cos, sin}

  def contour(t: Double) = (contourX(t), contourY(t))
  def contourX(t: Double) = 0.5 * cos(t) - 0.25 * cos(t * 2)
  def contourY(t: Double) = 0.5 * sin(t) - 0.25 * sin(2 * t)

  def apply(numberOfPoints: Int): Mandelbrot.Sequence => Double = {
    val points = (0 to numberOfPoints)
      .map(_ * math.Pi / numberOfPoints)
      .map(contour)

    def minimalDistance(v: Double, x: Double, _y: Double): Double = {
      val y = _y.abs
      points.foldLeft(v) {
        case (d, (px, py)) => d.min(q(px - x) + q(py - y))
      }
    }

    seq => seq.foldLeft(minimalDistance(2, _, _))(minimalDistance)
  }
}


object CardioidNumeric extends MathUtils{
  import Math.{cos, sin, sqrt}

  def contour(t: Double) = (contourX(t), contourY(t))
  def contourX(t: Double) = 0.5 * cos(t) - 0.25 * cos(t * 2)
  def contourY(t: Double) = 0.5 * sin(t) - 0.25 * sin(2 * t)

  def dist(t: Double, x: Double, y: Double): Double =
    sqrt(q(contourX(t) - x) + q(contourY(t) - y))

  def distSquared(t: Double, x: Double, y: Double): Double =
    q(contourX(t) - x) + q(contourY(t) - y)


  // sqrt( (cos(t)/2 - cos(2t)/4-x)^2 + (sin(t)/2 - sin(2t)/4-y)^2 )
  // d/dt sqrt( (cos(t)/2 - cos(2t)/4-x)^2 + (sin(t)/2 - sin(2t)/4-y)^2 )
  // => (2 (Cos[t]/2 - Cos[2 t]/2) (-y + Sin[t]/2 - Sin[2 t]/4) + 2 (-x + Cos[t]/2 - Cos[2 t]/4) (-Sin[t]/2 + Sin[2 t]/2))/(2 Sqrt[(-x + Cos[t]/2 - Cos[2 t]/4)^2 + (-y + Sin[t]/2 - Sin[2 t]/4)^2])

  // d/dt d/dt sqrt( (cos(t)/2 - cos(2t)/4-x)^2 + (sin(t)/2 - sin(2t)/4-y)^2 )
  // => (2*(c2-c1/2)*(c1/2-c2/4-x)+2*(s2-s1/2)*(s1/2-s2/4-y)+2*q(s2/2-s1/2)+2*q(c1/2-c2/2))/(2*sqrt(q(c1/2-c2/4-x)+q(s1/2-s2/4-y)))-q(2*(s2/2-s1/2)*(c1/2-c2/4-x)+2*(c1/2-c2/2)*(s1/2-s2/4-y))/(4*Math.pow(q(c1/2-c2/4-x)+q(s1/2-s2/4-y), 1.5))

  //  def d_derived(t: Double, x: Double, y: Double): Double = {
  //    val h = 0.0001
  //    (dist(t + h, x, y) - dist(t, x, y)) / h
  //  }
  //
  //  def d_derived2(t: Double, x: Double, y: Double): Double = {
  //    val h = 0.0001
  //    (dist(t + 2 * h, x, y) - 2 * dist(t + h, x, y) + dist(t, x, y)) / (h * h)
  //  }
  //
  //  def d_derived_ana(t: Double, x: Double, y: Double): Double = {
  //    val c1 = cos(t)
  //    val c2 = cos(t * 2)
  //    val s1 = sin(t)
  //    val s2 = sin(t * 2)
  //    (2 * (cos(t) / 2 - cos(2 * t) / 2) * (-y + sin(t) / 2 - sin(2 * t) / 4) + 2 * (-x + cos(t) / 2 - cos(2 * t) / 4) * (-sin(t) / 2 + sin(2 * t) / 2)) / (2 * sqrt(q(-x + cos(t) / 2 - cos(2 * t) / 4) + q(-y + sin(t) / 2 - sin(2 * t) / 4)))
  //  }
  //
  //  def d_derived2_ana(t: Double, x: Double, y: Double): Double = {
  //    val c1 = cos(t)
  //    val c2 = cos(t * 2)
  //    val s1 = sin(t)
  //    val s2 = sin(t * 2)
  //
  //    ((c2 - c1 / 2) * (c1 / 2 - c2 / 4 - x) + (s2 - s1 / 2) * (s1 / 2 - s2 / 4 - y) + q(s2 / 2 - s1 / 2) + q(c1 / 2 - c2 / 2)) / sqrt(q(c1 / 2 - c2 / 4 - x) + q(s1 / 2 - s2 / 4 - y)) -
  //      q((s2 - s1) * (c1 / 2 - c2 / 4 - x) + (c1 - c2) * (s1 / 2 - s2 / 4 - y)) / (4 * Math.pow(q(c1 / 2 - c2 / 4 - x) + q(s1 / 2 - s2 / 4 - y), 1.5))
  //  }

  // calculates [d/dt dist(t, x, y)] / [d/dt^2 dist(t, x, y)]
  def der1DivDer2(t: Double, x: Double, y: Double) = {
    val c1 = cos(t)
    val c2 = cos(t * 2)
    val c1h = c1 / 2
    val c2h = c2 / 2
    val c2hh = c2 / 4

    val s1 = sin(t)
    val s2 = sin(t * 2)
    val s1h = s1 / 2
    val s2h= s2 / 2
    val s2hh = s2 / 4

    val cd = c1h - c2h
    val sd = s2h - s1h
    val ct = c1h - c2hh - x
    val st = s1h - s2hh - y

    val d1 = cd * st + ct * sd
    val d2 = (c2 - c1h) * ct + (s2 - s1h) * st + q(sd) + q(cd) - q(d1) / (q(ct) + q(st))
    d1 / d2
  }

  def newton(iterations: Int)(t0: Double, x: Double, y: Double): Double = {
    var t = t0
    for (i <- 0 until iterations)
      t -= der1DivDer2(t, x, y)
    t
  }

  final val phi = (Math.sqrt(5) + 1) / 2
  def golden(iterations :Int)(x: Double, _y: Double): Double = {
    val y = _y.abs
    var a = 0.0
    var b = Math.PI
    for (i <- 0 until iterations) {
      val c = b - (b - a) / phi
      val d = a + (b - a) / phi
      if (distSquared(c, x, y) < distSquared(d, x, y))
        b = d
      else
        a = c
    }
    (a + b) / 2
  }


  def minimalDistance(iterations :Int)(x: Double, _y: Double): Double = {
    val y = _y.abs
    val t0 = golden(iterations)(x, y)
    //      val n = newton(t0, x, y)
    distSquared(t0, x, y)
  }

  def apply(newtonIterations: Int): Mandelbrot.Sequence => Double = {
    seq => seq.foldLeft(minimalDistance(newtonIterations)) {
      (v, x, y) => v.min(minimalDistance(newtonIterations)(x, y))
    }
  }
}