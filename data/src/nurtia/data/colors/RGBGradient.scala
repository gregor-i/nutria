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

package nurtia.data.colors

import nutria.core.Color
import nutria.core.colors.RGB

object RGBGradient {
  val default = RGBGradient(
    RGB.byHex(0x00000000),
    RGB.byHex(0x000000ff),
    RGB.byHex(0x0000ffff),
    RGB.byHex(0x00ffffff)
  )
}


case class RGBGradient(colors: RGB*) extends Color[Double] {
  private val n = colors.length - 1
  require(n > 0)

  override def apply(input: Double): RGB = {
    if (input <= 0) return colors(0)
    if (input >= 1) return colors(n)
    val rest = input * n
    val position = rest.toInt
    RGB.interpolate(colors(position), colors(position + 1), rest % 1)
  }
}
