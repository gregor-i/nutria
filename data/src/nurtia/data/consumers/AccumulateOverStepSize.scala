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

package nurtia.data.consumers

import nutria.core.accumulator.{Accumulator, Max, Min}
import nutria.core.{DoubleSequence, MathUtils}

abstract class AccumulateOverStepSize[A <: Accumulator](val accu: A) extends MathUtils {
  private case class Step(lastX: Double, lastY: Double, count: Int = 0, accumulatorState: accu.State = accu.neutral) {
    def next(x: Double, y: Double): Step = {
      val d = q(x - lastX) + q(y - lastY)
      Step(x, y, count + 1, accu.fold(accumulatorState, d))
    }

    def eval: Double = accu.lastOperation(accumulatorState, count)
  }

  def apply[S <: DoubleSequence](): S => Double =
    _.foldLeft(Step(_, _)) { case (step, x, y) => step.next(x, y) }.eval
}

object BiggestStep extends AccumulateOverStepSize(Max)

object SmallestStep extends AccumulateOverStepSize(Min)

