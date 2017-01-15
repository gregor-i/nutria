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

package nutria.core.colors

import nutria.core.Color

trait HSV[A] extends Color[A] {
  def H(lambda: A): Double
  def S(lambda: A): Double
  def V(lambda: A): Double

  def apply(lambda: A): RGB = HSV.HSV2RGB(H(lambda), S(lambda), V(lambda))
}

object HSV {
  def HSV2RGB(H: Double, S: Double, V: Double): RGB = {
    require(0 <= H && H <= 360, s"H = $H  is not in [0; 360]")
    require(0 <= S && S <= 1, s"S = $S  is not in [0; 1]")
    require(0 <= V && V <= 1, s"V = $V  is not in [0; 1]")

    val h = (H / 60).toInt
    val f = H / 60.0 - h
    val q = V * (1 - S * f)
    val p = V * (1 - S)
    val t = V * (1 - S * (1 - f))

    h % 6 match {
      case 0 => RGB(V*255, t*255, p*255)
      case 1 => RGB(q*255, V*255, p*255)
      case 2 => RGB(p*255, V*255, t*255)
      case 3 => RGB(p*255, q*255, V*255)
      case 4 => RGB(t*255, p*255, V*255)
      case 5 => RGB(V*255, p*255, q*255)
    }
  }
}


