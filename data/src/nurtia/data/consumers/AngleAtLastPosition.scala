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

import nurtia.data.sequences.Newton
import nutria.core.DoubleSequence
import nutria.core.colors.{HSV, RGB}
import spire.implicits._
import spire.math.Complex

import scala.util.control.NonFatal

object AngleAtLastPosition {
  def apply[A <: DoubleSequence](): A => Double =
    seq => {
      while (seq.next()) ()
      val (x, y) = seq.public
      Math.atan2(x, y)
    }
}

object NewtonColoring {
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

  def smooth[A <: Newton, B <: A#Sequence](newton:A): B => RGB =
    seq => try{
      var i = 1

      var last = seq.public
      while (seq.next()) {
        if(seq.hasNext)
          last = seq.public
        i += 1
      }

      val now  =seq.public


      val a = Math.atan2(now._1, now._2)

      val logT = Math.log(newton.threshold)
      val logD0 = Math.log(newton.f(Complex(now._1, now._2)).abs)
      val logD1 = Math.log(newton.f(Complex(last._1, last._2)).abs)

      val s = i -(logT - logD0) / (logD1 - logD0)

      val H = (a / Math.PI * 180 + 360) % 360
      val S = Math.exp(-s / 25d)
      val V = 1

      HSV.HSV2RGB(H, S, S)
    }catch {
      case NonFatal(_) => RGB.black
    }
}