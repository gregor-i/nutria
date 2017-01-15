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

package nurtia.data.fractalFamilies

import nurtia.data.consumers._
import nurtia.data.sequences.{ExperimentalNewton, Newton, ThreeRoots}
import nutria.core.syntax._
import nutria.core.viewport.Point
import nutria.core.{ContentFunction, RGB, Viewport}

import scala.util.control.NonFatal

abstract class NewtonData[N <: Newton](val name: String,
                                       val newton:N) extends Data[N#Sequence] {

  override val exampleSequenceConstructor: ContentFunction[N#Sequence] = newton(50)

  val initialViewport: Viewport = Viewport(Point(-2.5, -1), Point(3.5, 0), Point(0, 2))

  val selectionViewports: Set[Viewport] = Set.empty

  def wrappInTry[A, B](f: A=>B, default:B): (A=>B) = a =>{
    try{
      f(a)
    }catch {
      case NonFatal(_) => default
    }
  }

  val selectionFractals: Seq[(String, ContentFunction[Double])] = Seq(
    "RoughColoring"            -> newton(50) ~> wrappInTry(RoughColoring.double(), Double.MaxValue),
    "SmallestStep"             -> newton(50) ~> wrappInTry(SmallestStep(), Double.MaxValue),
    "AngleALastPosition"       -> newton(50) ~> wrappInTry(AngleAtLastPosition(), Double.MaxValue),
    "GaussianInteger"          -> newton(50) ~> wrappInTry(GaussianIntegerTraps(), Double.MaxValue)
  )
  override val directFractals: Seq[(String, ContentFunction[RGB])] = Seq(
    "NewtonColoring"           -> newton(50) ~> NewtonColoring(),
    "NewtonColoring.smooth"    -> newton(50) ~> NewtonColoring.smooth(newton)
  )
}

object ThreeRootsNewtonData extends NewtonData[ThreeRoots.type]("ThreeRoots", ThreeRoots)
object ExperimentalNewtonData extends NewtonData[ExperimentalNewton.type]("Experimental", ExperimentalNewton)