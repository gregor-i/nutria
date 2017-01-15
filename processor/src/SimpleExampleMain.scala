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

import nurtia.data.DimensionInstances
import nurtia.data.consumers.RoughColoring
import nurtia.data.fractalFamilies.MandelbrotData
import nurtia.data.sequences.Mandelbrot
import nutria.core.colors.Wikipedia
import nutria.core.image.DefaultSaveFolder
import nutria.core.syntax._

object SimpleExampleMain extends App {

  val saveFolder = DefaultSaveFolder

  MandelbrotData.initialViewport
    .withDimensions(DimensionInstances.fujitsu.scale(0.5))
    .withFractal(Mandelbrot(350, 10d) ~> RoughColoring.double())
    .linearNormalized
    .withColor(Wikipedia.repeated(1))
    .verboseSave(saveFolder /~ "basic.png")

  MandelbrotData.initialViewport
    .withDimensions(DimensionInstances.fujitsu.scale(0.5))
    .withAntiAliasedFractal(Mandelbrot(350, 10d) ~> RoughColoring.double())
    .linearNormalized
    .withDefaultColor
    .verboseSave(saveFolder /~ "aa.png")
}
