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

import nutria.core.sequences._
import spire.math.Quaternion

object Collection {

  val factories = Seq(
    SimpleFactory, AntiAliaseFactory, BuddhaBrotFactory
  )

  val doubleSequenceFractals: Seq[Data[_ <: DoubleSequence]] =
    Seq(
      MandelbrotData,
      MandelbrotCubeData,
      TricornData,
      CollatzData,
      BurningShipData,
      JuliaSetData(-0.6, -0.6),
      JuliaSetData(-0.4, 0.6),
      JuliaSetData(-0.8, 0.156),
      ThreeRootsNewtonData,
      ExperimentalNewtonData
    )

  val abstractFracals: Seq[Data[_]] =
    Seq(
      NovaData,
      new QuaternionBrotData("QuaternionBrot(x, y, 0, 0)", (x, y) => Quaternion(x, y, 0, 0)),
      new QuaternionBrotData("QuaternionBrot(x, y, 0.5, 0.5)", (x, y) => Quaternion(x, y, 0.5, 0.5)),
      new QuaternionBrotData("QuaternionBrot(x, y, 0.5, 0)", (x, y) => Quaternion(x, y, 0.5, 0)),
      new QuaternionBrotData("QuaternionBrot(strange)", (x, y) => Quaternion(x, y, x, x))
    )
}
