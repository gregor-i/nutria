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

package nutria.core.color

import nutria.core.Color

object RGB {
  val default = new Gradient(
    RGB.byHex(0x00000000),
    RGB.byHex(0x000000ff),
    RGB.byHex(0x0000ffff),
    RGB.byHex(0x00ffffff)
  )

  val white = RGB.byHex(0x00ffffff)
  val black = RGB.byHex(0x00000000)

  val corners = Set(
    white,
    RGB.byHex(0x000000ff),
    RGB.byHex(0x0000ff00),
    RGB.byHex(0x0000ffff),
    RGB.byHex(0x00ff0000),
    RGB.byHex(0x00ff00ff),
    RGB.byHex(0x00ffff00),
    black)

  def interpolate(la: RGB, lb: RGB, p: Double): RGB = {
    require(0 <= p && p <= 1, s"$p was not in the expected interval [0, 1]")
    val q = 1 - p
    RGB(la.R * q + lb.R * p,
        la.G * q + lb.G * p,
        la.B * q + lb.B * p)
  }

  def byHex(hex: Int): RGB = RGB((hex >> 16) & 0xff, (hex >> 8) & 0xff, 0xff & hex)
}

final case class RGB(R:Double, G:Double, B:Double) {
  require((0 <= R && R < 256) && (0 <= G && G < 256) && (0 <= B && B < 256), s"Requirement for RGB failed. input: R=$R, G=$G, B=$B")
  val hex: Int = R.toInt << 16 | G.toInt << 8 | B.toInt
  override def toString: String = "#%02x%02x%02x".format(0xff & R.toInt, 0xff & G.toInt, 0xff & B.toInt)
}

case class Gradient(colors: RGB*) extends Color[Double] {
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
