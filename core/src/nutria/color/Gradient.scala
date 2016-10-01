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

package nutria.color

case class Gradient(farben: ConstantColor*) extends Color {
  private val n = farben.length - 1
  require(n > 0)

  override def apply(input: Double): Int = {
    if (input <= 0) return farben(0).farbe
    if (input >= 1) return farben(n).farbe
    val rest = input * n
    val stelle = rest.toInt
    ConstantColor.interpolate(farben(stelle), farben(stelle + 1), rest % 1)
  }

  override def toString(): String = {
    String.format("GradientN %s", farben.toString())
  }
}
