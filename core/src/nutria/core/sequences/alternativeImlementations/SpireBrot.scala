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

package nutria.core.sequences.alternativeImlementations

import nutria.core.ContentFunction
import nutria.core.sequences.AbstractSequence
import spire.implicits._
import spire.math.Complex


object SpireBrot {

  final class Sequence(val start: Complex[Double], private var iterationsRemaining: Int) extends AbstractSequence {
    private var current: Complex[Double] = Complex.zero

    def hasNext: Boolean = current.abs < 2 && iterationsRemaining >= 0

    def next(): Boolean = {
      current = current * current + start
      iterationsRemaining -= 1
      hasNext
    }
  }

  def apply(maxIterations:Int):ContentFunction[Sequence] = (x0, y0) => new Sequence(Complex(x0, y0), maxIterations)
}
