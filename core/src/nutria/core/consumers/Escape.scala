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

import nutria.core.AbstractSequence
import nutria.core.sequences.DoubleSequence

object RoughColoring {
  def apply[A <: AbstractSequence](): A => Int =
    seq => {
      var i = 0
      while (seq.next()) i = i + 1
      i
    }

  def double[A <: AbstractSequence](): A => Double =
    apply().andThen(_.toDouble)
}


object SmoothColoring {
  def apply[A <: DoubleSequence](): A => Double =
    seq => seq.foldLeft((_, _) => 0d) {
      (v, x, y) => v + Math.exp(-(x * x + y * y))
    }
}