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

import nutria.core.consumers.CardioidNumeric
import org.scalacheck.Gen._

object SequenceChooser {
  val chooseAngle = choose(0d, Math.PI * 2)

  val chooseFromUsefullStartPoints = for {
    a <- chooseAngle
    r <- choose(0d, 5d)
  } yield (Math.sin(a) * r, Math.cos(a) * r)

  val chooseFromPointsOutsideOfTheEscapeRadius = for {
    a <- chooseAngle
    r <- choose(2d, 10d)
  } yield (Math.sin(a) * r, Math.cos(a) * r)

  val chooseFromTheCardioidContour = chooseAngle.map(CardioidNumeric.contour)

  val chooseFromTheInsideOfTheCardioid = for {
    (cx, cy) <- chooseFromTheCardioidContour
    f <- choose(0d, 0.95)
  } yield (cx * f, cy * f)

  val chooseFromPointsInsideOfP2 = for {
    a <- chooseAngle
    r <- choose(0d, 1d)
  } yield (Math.sin(a) * r * 0.25 - 1, Math.cos(a) * r * 0.25)
}
