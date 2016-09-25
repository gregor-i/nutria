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

abstract class BinaryAccumulator(val _left: Accumulator, val _right: Accumulator) extends Accumulator {
  override type State = (_left.State, _right.State)

  override def neutral: State = (_left.neutral, _right.neutral)
  override def fold(input: State, next: Double): State =
    (_left.fold(input._1, next), _right.fold(input._2, next))
}

case class Norm(left: Accumulator, right: Accumulator) extends BinaryAccumulator(left, right) {
  override def lastOperation(result: State, count:Int): Double = {
    val l = _left.lastOperation(result._1, count)
    val r = _right.lastOperation(result._2, count)
    Math.sqrt(l*l + r*r)
  }
}

case class Add(left: Accumulator, right: Accumulator) extends BinaryAccumulator(left, right) {
  override def lastOperation(result: State, count:Int): Double = {
    val l = _left.lastOperation(result._1, count)
    val r = _right.lastOperation(result._2, count)
    l+r
  }
}

case class Sub(left: Accumulator, right: Accumulator) extends BinaryAccumulator(left, right) {
  override def lastOperation(result: State, count:Int): Double = {
    val l = _left.lastOperation(result._1, count)
    val r = _right.lastOperation(result._2, count)
    l-r
  }
}
