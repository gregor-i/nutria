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

import org.scalacheck.Prop.forAll
import org.specs2.{ScalaCheck, Specification}
import org.scalacheck.Gen.choose

class ViewportSpec extends Specification with ScalaCheck {
  def is = s2"""
        Viewports describe parts of the complex plane.
        The parts are parallelograms. The 4 corners are:
          Origin
          Origin + A
          Origin + A + B
          Origin + B

        The Viewports can be moved and zoomed:
          move has invers operations $moveHasInversOperations
      """


  import ViewportChooser._

  def moveHasInversOperations = forAll(chooseViewport, choose(-10d, 10d)){
    (viewport, factor) => viewport.right(factor).left(factor) === viewport
  }
}
