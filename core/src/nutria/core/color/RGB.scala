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
    new ConstantColor(0x00000000),
    new ConstantColor(0x000000ff),
    new ConstantColor(0x0000ffff),
    new ConstantColor(0x00ffffff)
  )

  val white = new ConstantColor(0x00ffffff)
  val black = new ConstantColor(0x00000000)

  val corners = Array(
    white,
    new ConstantColor(0x000000ff),
    new ConstantColor(0x0000ff00),
    new ConstantColor(0x0000ffff),
    new ConstantColor(0x00ff0000),
    new ConstantColor(0x00ff00ff),
    new ConstantColor(0x00ffff00),
    black)

  def interpolate(la: ConstantColor, lb: ConstantColor, p: Double): Int = {
    val a = la.color
    val a0 = 0xff & a
    val a1 = 0xff & (a >> 8)
    val a2 = 0xff & (a >> 16)
    val a3 = 0xff & (a >> 24)
    val b = lb.color
    val b0 = 0xff & b
    val b1 = 0xff & (b >> 8)
    val b2 = 0xff & (b >> 16)
    val b3 = 0xff & (b >> 24)
    val q = 1 - p
    (a3 * q + b3 * p).toInt << 24 | (a2 * q + b2 * p).toInt << 16 | (a1 * q + b1 * p).toInt << 8 | (a0 * q + b0 * p).toInt
  }

  def apply(R: Double, G: Double, B: Double): Int = {
    assert(R >= 0 && R <= 1)
    assert(G >= 0 && G <= 1)
    assert(B >= 0 && B <= 1)
    (R * 255).toInt * (256 * 256) + (G * 255).toInt * (256) + (B * 255).toInt
  }
}

@SerialVersionUID(1L)
final case class ConstantColor(color: Int) extends Color {
  override def apply(in: Double): Int = color

  override def toString: String =
    "#%02x%02x%02x".format(0xff & (color >> 16), 0xff & (color >> 8), 0xff & color)
}


case class Gradient(colors: ConstantColor*) extends Color {
  private val n = colors.length - 1
  require(n > 0)

  override def apply(input: Double): Int = {
    if (input <= 0) return colors(0).color
    if (input >= 1) return colors(n).color
    val rest = input * n
    val position = rest.toInt
    RGB.interpolate(colors(position), colors(position + 1), rest % 1)
  }

  override def toString(): String = {
    String.format("GradientN %s", colors.toString())
  }
}
