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

package nurtia.data

import nutria.core._
import nutria.core.syntax._

trait ContentFactory {
  def apply(viewport: Viewport, dim: Dimensions, fractal: ContentFunction[Double], color: Color[Double]): Image
}

case object SimpleFactory extends ContentFactory {
  def apply(view: Viewport, dim: Dimensions, fractal: ContentFunction[Double], color: Color[Double]): Image =
    view.withDimensions(dim).withFractal(fractal).strongNormalized.withColor(color)
}

case class DirectFactory(contentFunction: ContentFunction[RGB]) extends ContentFactory {
  def apply(view: Viewport, dim: Dimensions, fractal: ContentFunction[Double], color: Color[Double]): Content[RGB] =
    view.withDimensions(dim).withFractal(contentFunction)
}

case object AntiAliaseFactory extends ContentFactory {
  def apply(view: Viewport, dim: Dimensions, fractal: ContentFunction[Double], color: Color[Double]): Image =
    view.withDimensions(dim).withAntiAliasedFractal(fractal).strongNormalized.withColor(color)
}

//case object BuddhaBrotFactory extends ContentFactory {
//  def apply(view: Viewport, dim: Dimensions, fractal: ContentFunction[Double], color: Color[Double]) =
//    view.withDimensions(dim).withBuddhaBrot().strongNormalized.withColor(color)
//}
