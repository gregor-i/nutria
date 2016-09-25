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

package nutria
package fractal.techniques

import java.lang.Math.sqrt

import nutria.fractal.{DoubleSequence, SequenceConstructor}

object TrapTechniques {
  def apply[A <: DoubleSequence : SequenceConstructor]: TrapTechniques[A] = new TrapTechniques[A]
}

class TrapTechniques[A <: DoubleSequence](implicit sequence: SequenceConstructor[A]) {
  @inline private[this] def q(@inline x: Double): Double = x * x

  def OrbitPoint(maxIteration: Int, trapx: Double, trapy: Double): Fractal =
    (x0, y0) =>
      sequence(x0, y0, maxIteration).foldLeft(q(x0 - trapx) + q(y0 - trapy)) {
        (d, x, y) => d.min(q(x - trapx) + q(y - trapy))
      }

  def OrbitImgAxis(maxIteration: Int): Fractal =
    (x0, y0) =>
      sequence(x0, y0, maxIteration).foldLeftY(y0.abs) {
        (d, y) => d.min(y.abs)
      }

  def OrbitRealAxis(maxIteration: Int): Fractal =
    (x0, y0) =>
      sequence(x0, y0, maxIteration).foldLeftX(x0.abs) {
        (d, x) => d.min(x.abs)
      }

  def CircleP2(maxIteration: Int) = CircleTrap(maxIteration, -1, 0, 0.25)

  def CircleTrap(maxIteration: Int, cx: Double, cy: Double, cr: Double): Fractal =
    (x0, y0) =>
      sequence(x0, y0, maxIteration).foldLeft((sqrt(q(x0 - cx) + q(y0 - cy)) - cr).abs) {
        (v, x, y) => v.min((sqrt(q(x - cx) + q(y - cy)) - cr).abs)
      }


  // not really a trap trechnique, so this needs to be moved.
  def SmoothColoring(maxIteration: Int): Fractal = (x, y) => {
    val seq = sequence(x, y, maxIteration)
    var accu = 0d
    while (seq.next())
      accu += Math.exp(-(seq.publicX * seq.publicX + seq.publicY * seq.publicY))
    accu
  }
}
