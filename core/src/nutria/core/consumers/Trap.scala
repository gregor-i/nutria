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

import nutria.core.sequences.{DoubleSequence, Mandelbrot}

object OrbitPoint {
  @inline private[this] def q(@inline x: Double): Double = x * x

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

object CircleTrap{
  @inline private[this] def q(@inline x: Double): Double = x * x

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