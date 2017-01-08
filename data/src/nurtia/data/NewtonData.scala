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

import nutria.core.consumers._
import nutria.core.sequences.{ExperimentalNewton, Newton, ThreeRoots}
import nutria.core.syntax._
import nutria.core.viewport.Point
import nutria.core.{ContentFunction, Viewport}

abstract class NewtonData[N <: Newton](val name: String,
                                       val exampleSequenceConstructor:ContentFunction[N#Sequence]) extends Data[N#Sequence] {

  val initialViewport: Viewport = Viewport(Point(-2.5, -1), Point(3.5, 0), Point(0, 2))

  val selectionViewports: Set[Viewport] = Set.empty

  val selectionFractals: Seq[(String, ContentFunction[Double])] = Seq(
    "RoughColoring"            -> exampleSequenceConstructor ~> RoughColoring.double(),
    "SmallestStep"             -> exampleSequenceConstructor ~> SmallestStep(),
    "AngleALastPosition"       -> exampleSequenceConstructor ~> AngleAtLastPosition()
  )
}

object ThreeRootsNewtonData extends NewtonData[ThreeRoots.type]("ThreeRoots", ThreeRoots(50))
object ExperimentalNewtonData extends NewtonData[ExperimentalNewton.type]("Experimental", ExperimentalNewton(50))