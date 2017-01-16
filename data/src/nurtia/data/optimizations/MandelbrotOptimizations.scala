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

package nurtia.data.optimizations

import nutria.core.ContentFunction

object MandelbrotOptimizations {
  def RoughColoringDouble(iterations:Int, escapeRadius:Double): ContentFunction[Double] = {
    val escapeOrbitSquared = escapeRadius * escapeRadius
    (x0, y0) => {
      var x = 0d
      var y = 0d
      var xx = 0d
      var yy = 0d
      var iterationsRemaining = iterations
      var c = 0
      while ( {
        y = 2 * x * y + y0
        x = xx - yy + x0
        xx = x * x
        yy = y * y
        iterationsRemaining -= 1
        xx + yy < escapeOrbitSquared && iterationsRemaining >= 0
      }) {
        c += 1
      }
      c
    }
  }

  def SmoothColoring(iterations:Int, escapeRadius:Double): ContentFunction[Double] = {
    val escapeOrbitSquared = escapeRadius * escapeRadius
    (x0, y0) => {
      var x = 0d
      var y = 0d
      var xx = 0d
      var yy = 0d
      var iterationsRemaining = iterations
      var accu = 0d
      while ( {
        y = 2 * x * y + y0
        x = xx - yy + x0
        xx = x * x
        yy = y * y
        iterationsRemaining -= 1
        xx + yy < escapeOrbitSquared && iterationsRemaining >= 0
      }) {
        accu += Math.exp(-(x * x + y * y))
      }
      accu
    }
  }
}