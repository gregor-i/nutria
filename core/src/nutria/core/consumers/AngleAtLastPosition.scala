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

import nutria.core.color.{HSV, RGB}
import nutria.core.sequences.DoubleSequence

import scala.util.control.NonFatal

object AngleAtLastPosition {
  def apply[A <: DoubleSequence](): A => Double =
    seq => {
      while (seq.next()) ()
      val (x, y) = seq.public
      Math.atan2(x, y)
    }
}

object DirectColors {
  def apply[A <: DoubleSequence](): A => RGB =
    seq => try{
      var i = 1
      while (seq.next()) i += 1
      val (x, y) = seq.public
      val a = Math.atan2(x, y)

      val H = (a / Math.PI * 180 + 360) % 360
      val S = Math.exp(-i / 25d)
      val V = S

      HSV.HSV2RGB(H, S, S)
    }catch {
      case NonFatal(_) => RGB.black
    }
}