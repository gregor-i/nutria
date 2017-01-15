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

package nutria.accumulator

import nutria.core.accumulator.Accumulator
import org.specs2.{ScalaCheck, Specification}

import scala.reflect.ClassTag
import scala.util.Random

case class AccumulatorLaws[A <: Accumulator : ClassTag](accumulator:A)(implicit ct:ClassTag[A]) extends Specification with ScalaCheck {
    def is = s"Accumulator Laws for ${ct.getClass.getSimpleName}" ^ s2"""
       Accumulators are independant of the order: $orderIndependant
      """

  def compareInclusiveNaN(a:Double, b:Double): Boolean = (a == a, b == b) match {
    case (false, false) => true
    case (true, _) | (_, true) => false
    case _ => a == b
  }

  def orderIndependant = prop{(ds: Seq[Double]) => compareInclusiveNaN(accumulator(ds) , accumulator(Random.shuffle(ds)))}
}
