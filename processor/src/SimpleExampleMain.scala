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

import nurtia.data.Defaults
import nurtia.data.consumers.RoughColoring
import nurtia.data.sequences.Mandelbrot
import nutria.core.colors.Wikipedia
import nutria.core.syntax._

object SimpleExampleMain extends App with Defaults {

  defaultViewport
    .withDimensions(default)
    .withFractal(Mandelbrot(350, 10d) ~> RoughColoring.double())
    .linearNormalized
    .withColor(Wikipedia.repeated(1))
    .verboseSave(defaultSaveFolder/~ "basic.png")

  defaultViewport
    .withDimensions(default)
    .withAntiAliasedFractal(Mandelbrot(350, 10d) ~> RoughColoring.double())
    .linearNormalized
    .withColor(default)
    .verboseSave(defaultSaveFolder /~ "aa.png")
}
