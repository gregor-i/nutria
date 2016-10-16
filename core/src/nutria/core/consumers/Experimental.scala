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

import nutria.core.sequences.DoubleSequence

object BiggestStep {
  def apply[A <: DoubleSequence](): A => Double =
    seq => {
      var lastX = seq.publicX
      var lastY = seq.publicY
      seq.next()
      seq.foldLeft(0d){
        (v, x, y) =>
          val d = (x-lastX)*(x-lastX)  + (y-lastY) *(y-lastY)
          lastX = x
          lastY = y
          v.max(d)
      }
    }
}

object SmallestStep {
  def apply[A <: DoubleSequence](): A => Double =
    seq => {
      var lastX = seq.publicX
      var lastY = seq.publicY
      seq.next()
      seq.foldLeft(Double.MaxValue){
        (v, x, y) =>
          val d = (x-lastX)*(x-lastX)  + (y-lastY) *(y-lastY)
          lastX = x
          lastY = y
          v.min(d)
      }
    }
}