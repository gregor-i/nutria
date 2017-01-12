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

import java.lang.Math.sqrt

import nutria.core.MathUtils
import nutria.core.sequences.{DoubleSequence, Mandelbrot}

object OrbitPoint extends MathUtils {
  def apply[A <: DoubleSequence](trapx: Double, trapy: Double): A => Double =
    _.foldLeft((x, y) => q(x - trapx) + q(y - trapy)) {
      (d, x, y) => d.min(q(x - trapx) + q(y - trapy))
    }
}

object OrbitImgAxis {
  def apply[A <: DoubleSequence](): A => Double =
    seq => seq.foldLeftY(_ => Double.MaxValue) {
      (d, y) => d.min(y.abs)
    }
}

object OrbitRealAxis {
  def apply[A <: DoubleSequence](): A => Double =
    seq => seq.foldLeftX(_ => Double.MaxValue) {
      (d, x) => d.min(x.abs)
    }
}

object OrbitBothAxis {
  def apply[A <: DoubleSequence](): A => Double =
    seq => seq.foldLeft((_, _) => Double.MaxValue) {
      (d, x, y) => d.min(x.abs.min(y.abs))
    }
}

object CircleTrap extends MathUtils{
  def apply[A <: DoubleSequence](cx: Double, cy: Double, cr: Double):A => Double =  {
    @inline def d(x: Double, y: Double) = (sqrt(q(x - cx) + q(y - cy)) - cr).abs
    _.foldLeft(d) {
      (v, x, y) => v.min(d(x, y))
    }
  }
}

object CircleP2{
  def apply():Mandelbrot.Sequence => Double = CircleTrap(-1, 0, 0.25)
}

object GaussianIntegerTraps extends MathUtils{
  @inline def distance(x:Double, y:Double): Double =
    q(x - Math.round(x)) + q(y - Math.round(y))

  def apply[A <: DoubleSequence]():A => Double = {
    _.foldLeft((_, _) =>Double.MaxValue) {
      (v, x, y) => v.min(distance(x, y))
    }
  }

  def withFadeout[A <: DoubleSequence]():A => Double = {
    _.foldLeft((_, _) => (1, Double.MaxValue)) {
      case ((i, v), x, y) => (i+1, v.min(distance(x, y)/i))
    }._2
  }
}