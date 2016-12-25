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

case class Transform(view: Viewport, dimensions: Dimensions) extends HasDimensions with ((Double,Double) => (Double, Double)) {
  val scaleX = view.A.x / width
  val scaleY = view.B.y / height
  val shearX = view.B.x / height
  val shearY = view.A.y / width
  val translationX = view.origin.x
  val translationY = view.origin.y

  def transformX(x: Double, y: Double): Double = scaleX * x + shearX * y + translationX
  def transformY(x: Double, y: Double): Double = shearY * x + scaleY * y + translationY
  def transform(x: Double, y: Double): (Double, Double) = (transformX(x, y), transformY(x,y))
  override def apply(x: Double, y: Double) = transform(x, y)

  // for multi-sampling.
  def transformX(x0: Int, y0: Int, xi: Int, yi: Int, multi: Double): Double = transformX(x0 + xi * multi, y0 + yi * multi)
  def transformY(x0: Int, y0: Int, xi: Int, yi: Int, multi: Double): Double = transformY(x0 + xi * multi, y0 + yi * multi)

  // def transformX(x0: Int, y0: Int, xo: Double, yo: Double): Double = transformX(x0 + xo, y0 + yo)
  // def transformY(x0: Int, y0: Int, xo: Double, yo: Double): Double = transformY(x0 + xo, y0 + yo)

  def invertX(x: Double, y: Double): Double =
    (scaleY * (x - translationX) - shearX * (y - translationY)) / (scaleX * scaleY - shearX * shearY)
  def invertY(x: Double, y: Double): Double =
    (scaleX * (y - translationY) - shearY * (x - translationX)) / (scaleX * scaleY - shearX * shearY)
  def invert(x: Double, y: Double): (Double, Double) = (invertX(x, y), invertY(x,y))
}
