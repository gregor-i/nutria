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

package nutria.core.image

import java.awt.image.BufferedImage

import nutria.core.viewport.HasDimensions
import nutria.core.{Color, FinishedContent}

class Image(val content: FinishedContent[Double], val farbe: Color) extends HasDimensions {
  val dimensions = content.dimensions

  val buffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
  for (w <- 0 until width; h <- 0 until height)
    buffer.setRGB(w, h, farbe(content(w, h)).hex)
}
