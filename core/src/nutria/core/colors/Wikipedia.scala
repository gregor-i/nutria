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

package nutria.core.colors

import nutria.core.Color

object Wikipedia extends Color[Double] {
  val values = List(
    0.0 -> RGB(0, 7, 100),
    0.16 -> RGB(32, 107, 203),
    0.42 -> RGB(237, 255, 255),
    0.6425 -> RGB(255, 170, 0),
    0.8575 -> RGB(0, 2, 0),
    1.0 -> RGB(0, 7, 100)
  )

  override def apply(key: Double): RGB = {
    require(key >= 0 && key <= 1)
    if (key == 0.0)
      values.head._2
    else if (key == 1.0)
      values.last._2
    else {
      assert(key > 0.0 && key < 1)
      val i = values.indexWhere(_._1 > key)
      assert(i != -1)
      assert(i != 0)
      val (keyLeft, colorLeft) = values(i - 1)
      val (keyRight, colorRight) = values(i)

      assert(keyLeft <= key)
      assert(keyRight >= key)

      RGB.interpolate(colorLeft, colorRight, (keyLeft - key) / (keyLeft - keyRight))
    }
  }
}
