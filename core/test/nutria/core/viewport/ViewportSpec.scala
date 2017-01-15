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

import org.scalacheck.Gen.choose
import org.scalacheck.Prop.forAll
import org.specs2.matcher.Matcher
import org.specs2.{ScalaCheck, Specification}

class ViewportSpec extends Specification with ScalaCheck {
  def is = s2"""
        A viewports describes a part of the complex plane.
        The part is a parallelogram and the corners are:
          Origin
          Origin + A
          Origin + A + B
          Origin + B

        The Viewports can be moved and zoomed:
          move has invers operations $moveHasInversOperations
          zoom has invers operations $zoomHasInversOperations
          focused to a point         $focused
      """


  import ViewportChooser._

  def ~==(left: Double, right: Double): Boolean = (left must beCloseTo(right within 2.significantFigures)).isSuccess
  def ~==(left: Point, right: Point): Boolean = ~==(left.x, right.x) && ~==(left.y, right.y)
  def beCloseTo(v1: => Viewport): Matcher[Viewport] = ((v2: Viewport) =>
    ~==(v1.A, v2.A) && ~==(v1.B, v2.B) && ~==(v1.origin, v2.origin),
    (v2: Viewport) => s"The Viewports $v1 and $v2 didn't match")


  def moveHasInversOperations = forAll(chooseViewport, choose(-10d, 10d)) {
    (viewport, factor) =>
      (viewport.right(factor).left(factor) must beCloseTo(viewport))
        .and(viewport.left(factor).right(factor) must beCloseTo(viewport))
        .and(viewport.up(factor).down(factor) must beCloseTo(viewport))
        .and(viewport.down(factor).up(factor) must beCloseTo(viewport))
  }

  def zoomHasInversOperations = forAll(chooseViewport, choose(-10d, 10d), choose(0d, 1d), choose(0d, 1d)) {
    (viewport, factor, zoomX, zoomY) =>
      (viewport.zoomIn((zoomX, zoomY)).zoomOut((zoomX, zoomY)) must beCloseTo(viewport))
        .and(viewport.zoomOut((zoomX, zoomY)).zoomIn((zoomX, zoomY)) must beCloseTo(viewport))
  }

  def focused = forAll(chooseViewport, choose(0d, 1d), choose(0d, 1d)) {
    (viewport, focusX, focusY) => {
      val Viewport(origin, a, b) = viewport
      val Viewport(fOrigin, fa, fb) = viewport.focus(focusX, focusY)
      (a == fa) && (b == fb) && ~==(origin + (a * focusX) + (b * focusY), fOrigin + (a * 0.5) + (b * 0.5))
    }
  }
}
