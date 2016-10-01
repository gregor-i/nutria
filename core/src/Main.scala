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

import nutria.color.HSV
import nutria.consumers.SmoothColoring
import nutria.sequences.Mandelbrot
import nutria.syntax._
import nutria.viewport.Dimensions

object Main extends App {

  val root = "E:\\snapshots\\"
//  start
//    .withDimensions(Dimensions.fujitsu)
//    .withDynamAntiAliasedFractal(EscapeTechniques[Sequence].RoughColoring(350))
//    .linearNormalized
//    .withColor(HSV.MonoColor.Blue)
//    .verboseSave(root + s"dynam.png")
//
//  start
//    .withDimensions(Dimensions.fujitsu)
//    .withAntiAliasedFractal(EscapeTechniques[Sequence].RoughColoring(350))
//    .linearNormalized
//    .withColor(HSV.MonoColor.Blue)
//    .verboseSave(root + s"aa.png")
//
//
//  start
//    .withDimensions(Dimensions.fujitsu)
//    .withFractal(EscapeTechniques[Sequence].RoughColoring(350))
//    .linearNormalized
//    .withColor(HSV.MonoColor.Blue)
//    .verboseSave(root + s"basic.png")

  Mandelbrot.start
    .withDimensions(Dimensions.fullHD)
    .withFractal(Mandelbrot(350) ~> SmoothColoring())
    .linearNormalized
    .withColor(HSV.MonoColor.Blue)
    .verboseSave(root + "consumerPattern.png")

}
