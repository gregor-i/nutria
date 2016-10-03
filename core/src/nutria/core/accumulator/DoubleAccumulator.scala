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

package nutria.core.accumulator

case object Arithmetic extends Accumulator {
  override type State = Double
  override val neutral: Double  = 0d
  override def fold(left: Double, right: Double): Double = left + right
  override def lastOperation(result: Double, count: Int): Double = result / count
}

case object Geometric extends Accumulator {
  override type State = Double
  override val neutral: Double = 1d
  override def fold(left: Double, right: Double): Double = left * right
  override def lastOperation(result: Double, count: Int): Double = Math.pow(result, 1d/count)
}

case object Harmonic extends Accumulator {
  override type State = Double
  override val neutral: Double = 0d
  override def fold(left: Double, right: Double): Double = left + 1d/right
  override def lastOperation(result: Double, count: Int): Double = count / result
}

case object Max extends Accumulator {
  override type State = Double
  override val neutral: Double = java.lang.Double.NEGATIVE_INFINITY
  override def fold(left: Double, right: Double): Double = Math.max(left, right)
  override def lastOperation(result: Double, count: Int): Double = result
}

case object Min extends Accumulator {
  override type State = Double
  override val neutral: Double = java.lang.Double.POSITIVE_INFINITY
  override def fold(left: Double, right: Double): Double = Math.min(left, right)
  override def lastOperation(result: Double, count: Int): Double = result
}
