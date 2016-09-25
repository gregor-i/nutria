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

case object Variance extends Accumulator {
  override type State = (Double, Double)
  override val neutral = (Arithmetic.neutral, Arithmetic.neutral)

  override def fold(left: (Double, Double), right: Double): (Double, Double) = {
    val fold1 = Arithmetic.fold(left._1, right)
    val fold2 = Arithmetic.fold(left._2, right * right)
    (fold1, fold2)
  }

  override def lastOperation(result: (Double, Double), count: Int): Double = {
    val result1 = Arithmetic.lastOperation(result._1, count)
    val result2 = Arithmetic.lastOperation(result._2, count)
    Math.sqrt(result2 - result1 * result1)
  }
}
