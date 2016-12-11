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
import java.io.File

import nutria.core.{Color, Image, NormalizedContent}

object Image {
  def apply[A](content: NormalizedContent[A], color: Color[A]): Image = content.map(color)

  def buffer(image: Image) = {
    val b = new BufferedImage(image.width, image.height, BufferedImage.TYPE_INT_RGB)
    for (w <- 0 until image.width; h <- 0 until image.height)
      b.setRGB(w, h, image(w, h).hex)
    b
  }

  def save(image: Image, file: java.io.File): File = {
    if (file.getParentFile != null)
      file.getParentFile.mkdirs()
    javax.imageio.ImageIO.write(buffer(image), "png", file)
    file
  }

  def verboseSave(image: Image, file: java.io.File): File = {
    save(image, file)
    println("Saved: " + file.getAbsoluteFile.toString)
    file
  }
}