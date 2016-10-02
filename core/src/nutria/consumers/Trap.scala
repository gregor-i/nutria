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

package nutria.consumers

import java.lang.Math.sqrt

import nutria._
import nutria.sequences.{DoubleSequence, Mandelbrot}


object OrbitPoint {
  @inline private[this] def q(@inline x: Double): Double = x * x

  def apply[A <: DoubleSequence](trapx: Double, trapy: Double): SequenceConsumer[A] =
    seq => seq.foldLeft(q(seq.publicX - trapx) + q(seq.publicY - trapy)) {
      (d, x, y) => d.min(q(x - trapx) + q(y - trapy))
    }
}

object OrbitImgAxis {
  def apply[A <: DoubleSequence](): SequenceConsumer[A] =
    seq => seq.foldLeftY(Double.MaxValue) {
      (d, y) => d.min(y.abs)
    }
}

object OrbitRealAxis {
  def apply[A <: DoubleSequence](): SequenceConsumer[A] =
    seq => seq.foldLeftX(Double.MaxValue) {
      (d, x) => d.min(x.abs)
    }
}

object OrbitBothAxis {
  def apply[A <: DoubleSequence](): SequenceConsumer[A] =
    seq => 1/seq.foldLeft(Double.MaxValue) {
      (d, x, y) =>
        if(x.abs < y.abs && x.abs < d.abs){
        (x.abs)
      }else if(y.abs < x.abs && y.abs < d.abs){
        -(y.abs)
      }else{
          d
        }
    }
}

object CircleTrap{
  @inline private[this] def q(@inline x: Double): Double = x * x

  def apply[A <: DoubleSequence](cx: Double, cy: Double, cr: Double):SequenceConsumer[A] =  {
    @inline def d(x: Double, y: Double) = (sqrt(q(x - cx) + q(y - cy)) - cr).abs
    seq => seq.foldLeft(d(seq.publicX, seq.publicY)) {
      (v, x, y) => v.min(d(x, y))
    }
  }
}

object CircleP2{
  def apply():SequenceConsumer[Mandelbrot.Sequence] = CircleTrap(-1, 0, 0.25)
}