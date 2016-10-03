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

package nutria.core.viewport

object Point {

  def doubleToString(d: Double): String = {
    java.lang.Long.toHexString(java.lang.Double.doubleToLongBits(d))
  }

  def longToDouble(l: Long): Double = java.lang.Double.longBitsToDouble(l)

  def linearIndependant(a: Point, b: Point): Boolean = {
    val lambda1 = a.x / b.x
    val lambda2 = a.y / b.y
    lambda1 != lambda2
  }

  def tupled(t:(Double, Double)):Point = Point(t._1, t._2)

  def createWithLongs(x: Long, y: Long) =
    new Point(longToDouble(x), longToDouble(y))
}

case class Point(x: Double, y: Double) {
  require(!java.lang.Double.isNaN(x))
  require(!java.lang.Double.isNaN(y))
  require(!java.lang.Double.isInfinite(x))
  require(!java.lang.Double.isInfinite(y))

  override def toString: String = {
    String.format("0x%sL, 0x%sL", Point.doubleToString(x), Point.doubleToString(y))
  }
  
  def +(px: Double, py: Double): Point = new Point(x + px, y + py)
  def -(px: Double, py: Double): Point = new Point(x - px, y - py)

  def +(t: Point): Point = this + (t.x, t.y)
  def -(t: Point): Point = this - (t.x, t.y)

  def *(f: Double): Point = new Point(x * f, y * f)
  def *(p: Point): Double = x * p.x + y * p.y

  def orth(): Point = new Point(y, -x)
  def norm(): Double = Math.sqrt(x * x + y * y)
}
