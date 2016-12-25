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

package nutria.core.viewport

import nutria.core.fractal.SequenceChooser.chooseFromUsefullStartPoints

object ViewportChooser {
  val chooseViewport = for {
    origin <- chooseFromUsefullStartPoints
    originPlusA <- chooseFromUsefullStartPoints
    originPlusB <- chooseFromUsefullStartPoints
  } yield Viewport(
    Point(origin._1, origin._2),
    Point(originPlusA._1 - origin._1, originPlusA._2 - origin._2),
    Point(originPlusB._1 - origin._1, originPlusB._2 - origin._2))
}
