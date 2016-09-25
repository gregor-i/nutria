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

import nutria.fractal.{AbstractSequence, SequenceConstructor}

object EscapeTechniques{
  def apply[A <: AbstractSequence:SequenceConstructor]: EscapeTechniques[A] = new EscapeTechniques[A]
}

class EscapeTechniques[A <: AbstractSequence](implicit sequence: SequenceConstructor[A]) {
  def RoughColoring(maxIteration: Int): Fractal = sequence(_, _, maxIteration).size().toDouble

  def Brot(maxIteration: Int): Fractal =
    (x, y) => if (sequence(x, y, maxIteration).size() == maxIteration) 1 else 0
}
