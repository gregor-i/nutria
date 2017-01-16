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

import java.io.File

import nurtia.data.colors.Wikipedia
import nurtia.data.fractalFamilies.MandelbrotData
import nutria.core.{Color, Dimensions, Viewport}
import nutria.core.image.SaveFolder

trait Defaults extends DefaultColor with DefaultDimensions with DefaultViewport with DefaultSaveFolder {
  def default[A](implicit a: A): A = a // just an alias for implicitly
}

trait DefaultColor {
  implicit val defaultColor : Color[Double] = Wikipedia
}

trait DefaultDimensions {
  implicit val defaultDimensions: Dimensions = DimensionInstances.fullHD
}

trait DefaultViewport {
  implicit val defaultViewport: Viewport = MandelbrotData.initialViewport
}

trait DefaultSaveFolder {
  val defaultSaveFolder:SaveFolder = SaveFolder(nutria.core.BuildInfo.defaultSaveFolder.getOrElse("." + File.separator))
}