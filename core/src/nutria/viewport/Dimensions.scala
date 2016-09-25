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

package nutria.viewport

case class Dimensions(width: Int, height: Int) {
  def scale(factor: Double) = Dimensions((width * factor).toInt, (height * factor).toInt)
}

object Dimensions {
  val fujitsu = Dimensions(1920, 1200)
  val fullHD = Dimensions(1920, 1080)
  val lenovoX1 = Dimensions(2560, 1440)
}

trait HasDimensions {
  def dimensions: Dimensions
  def width = dimensions.width
  def height = dimensions.height
}
