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

package nutria.fractal.alternativeImplementions

import nutria.Fractal
import spire.math.Complex
import spire.implicits._

object StreamBrot {
  def RoughColoring(maxIterations:Int):Fractal =
    (x0, y0) => {
      val start = Complex(x0,y0)
      def seq: Stream[Complex[Double]] = Complex.zero[Double] #:: seq.map(c => c * c + start)
      seq.zipWithIndex.takeWhile{ case (v, i) => v.abs < 2 && i < maxIterations }.size
    }
}
