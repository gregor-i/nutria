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

package nutria.core.fractal

import nutria.core.sequences.Mandelbrot
import nutria.core.consumers.RoughColoring
import nutria.core.syntax._
import SequenceChooser._
import org.scalacheck.Gen.choose
import org.scalacheck.Prop.forAll
import org.specs2.{ScalaCheck, Specification}

class MandelbrotSpec extends Specification with ScalaCheck {
  def is =
    s2"""
       All points inside the cardioid start an infinite sequence                   $insideCardioid
       All points inside the P2 Circle start an initite sequence                   $insideP2
       The sequence starting at (0, 0) repeats infinitely                          $zero

       All sequences start at (0,0)                                                $startAtZero
       All sequences starting at (x,y) are at (x,y) after 1 iteration              $afterOneIteration
       All sequences starting at p with p.abs < 2 terminate directly               $staringOutside
       All sequences starting at different points as (0,0) next is not idenpotent  $noRepeating
    """

  val iterations = 1000
  val escapeRadius = 2d

  private def sequence = Mandelbrot(iterations, escapeRadius)
  private def roughColoring = sequence ~> RoughColoring()


  def insideCardioid = forAll(chooseFromTheInsideOfTheCardioid) {
    case (x: Double, y: Double) =>
      roughColoring(x, y) === iterations
  }

  def insideP2 = forAll(chooseFromPointsInsideOfP2) {
    case (x, y) =>
      roughColoring(x, y) === iterations
  }

  def zero = forAll(choose(0, iterations)) {
    (i) =>
      val seq = sequence(0, 0)
      for (_ <- 0 until i) seq.next()
      seq.publicX === 0 and seq.publicY === 0
  }

  def startAtZero = forAll(chooseFromUsefullStartPoints) {
    case (x, y) =>
      val seq = sequence(x, y)
      seq.publicX === 0 and seq.publicY === 0
  }

  def afterOneIteration = forAll(chooseFromUsefullStartPoints) {
    case (x, y) =>
      val seq = sequence(x, y)
      seq.next()
      seq.publicX === x and seq.publicY === y
  }

  def staringOutside = forAll(chooseFromPointsOutsideOfTheEscapeRadius) {
    case (x, y) =>
      val seq = sequence(x, y)
      seq.next()
      seq.hasNext === false
  }

  def noRepeating = forAll(chooseFromUsefullStartPoints) {
    case (x, y) =>
      val seq = sequence(x, y)
      val oldState = seq.public
      seq.next()
      oldState !== seq.public
  }
}