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

package nurtia.data.consumers

import nurtia.data.sequences.Mandelbrot
import nutria.core.MathUtils

import scala.annotation.tailrec

trait CardioidUtils extends MathUtils {
  import Math.{cos, sin, sqrt}

  @inline final def contour(t: Double): (Double, Double) = (contourX(t), contourY(t))
  @inline final def contourX(t: Double): Double = 0.5 * cos(t) - 0.25 * cos(2 * t)
  @inline final def contourY(t: Double): Double = 0.5 * sin(t) - 0.25 * sin(2 * t)

  @inline final def distSquared(t: Double, x: Double, y: Double): Double =
    q(contourX(t) - x) + q(contourY(t) - y)

  @inline final def dist(t: Double, x: Double, y: Double): Double =
    sqrt(distSquared(t, x, y))
}

object CardioidHeuristic extends CardioidUtils {
  def apply(numberOfPoints: Int): Mandelbrot.Sequence => Double = {
    val points = (0 to numberOfPoints)
      .map(_ * math.Pi / numberOfPoints)
      .map(contour)

    def minimalDistance(v: Double, x: Double, _y: Double): Double = {
      val y = _y.abs
      def d(p:(Double, Double)): Double = q(p._1 - x) + q(p._2 - y)
      d(points.minBy(d))
    }

    seq => seq.foldLeft(minimalDistance(2, _, _))(minimalDistance)
  }
}


object CardioidNumeric extends CardioidUtils {
  import Math.{cos, sin, sqrt}

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

  def newton(iterations: Int)(lower: Double, upper:Double)(x: Double, y: Double): Double = {
    var t = (lower + upper) /2
    for (i <- 0 until iterations) {
      t -= der1DivDer2(t, x, y)
      if(t > upper || t < lower)
        throw new ArithmeticException(s"Newton does not converge. After $i iterations.")
    }
    t
  }

  final val phi:Double = (sqrt(5) + 1) / 2
  // This algorithm has is flaw:
  // For some inputs it yields an incorrect output. In this cases t=0 is a better solution.
  // But the good thing is, that it's always exactly t=0, so it can be checked with in a single calculation.
  def golden(iterations:Int)(x:Double, _y:Double): (Double, Double) = {
    val y = _y.abs
    def calc(t:Double):Double = distSquared(t, x, y)
    @tailrec def loop(i:Int, a:Double, fa:Double, b:Double, fb:Double): (Double, Double) =
      if(i == 0)
        (a, b)
      else {
        val c = b - (b - a) / phi
        val d = a + (b - a) / phi
        val fc = calc(c)
        val fd = calc(d)
        if (fc < fd)
          loop(i - 1, a, fa, d, fd)
        else
          loop(i - 1, c, fc, b, fb)
      }
    loop(iterations-1, 0, calc(0), Math.PI, calc(Math.PI))
  }

  def minimalDistance(iterations :Int)(x: Double, _y: Double): Double = {
    val y = _y.abs
    val (lower, upper) = golden(iterations)(x, y)
    val t = (lower + upper) / 2
    val d = distSquared(t, x, y)
    val d0 = distSquared(0, x, y)
    if(d > d0) d0
    else d
  }

  def apply(newtonIterations: Int): Mandelbrot.Sequence => Double =
    _.foldLeft((_, _) => Double.MaxValue) {
      (v, x, y) => v.min(minimalDistance(newtonIterations)(x, y))
    }
}