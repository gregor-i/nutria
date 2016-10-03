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

object Viewport {

  def createViewportByLongs(x0: Long, y0: Long, ax: Long, ay: Long, bx: Long, by: Long) =
    Viewport(Point.createWithLongs(x0, y0),
      Point.createWithLongs(ax, ay),
      Point.createWithLongs(bx, by))

  def createViewportCentered(a: Point, b: Point): Viewport = {
    val diff = b - a
    val orth = diff.orth()
    val U = a - diff - orth
    val B = orth * 2
    val A = diff * 3
    Viewport(U, A, B)
  }

  def createByFocus(FA: Point, FB: Point)(a: Point, b: Point): Viewport = {
    val Fdelta = FB - FA
    val angle = Math.acos(Fdelta.y / Fdelta.norm())

    def rotAngle(p: Point): Point = {
      val c = Math.cos(angle)
      val s = Math.sin(angle)
      Point(c * p.x - s * p.y, s * p.x + c * p.y)
    }

    val diff = b - a
    val norm = diff.norm()
    val rot = rotAngle(diff)
    val rotOrth = rot.orth()

    val A = rot * ((rot * diff) / (norm * norm))
    val B = rotOrth * ((rotOrth * diff) / (norm * norm))

    val TA = A * (1.0 / Fdelta.y)
    val TB = B * (1.0 / Fdelta.x)
    val U = a - TA * FA.y - TB * FA.x

    Viewport(U, TA, TB)
  }

  def createByDefaultFocusAndLongs(ax: Long, ay: Long, bx: Long, by: Long) = createByFocus(Point(0.3, 0.1), Point(0.7, 0.3))(Point.createWithLongs(ax, ay), Point.createWithLongs(bx, by))


  val defaultMovementFactor: Double = 0.20
  val defaultZoomFactor: Double = 0.40
  val invFactorZoom: Double = 1.0 / (1.0 - defaultZoomFactor)

}

case class Viewport(origin: Point, A: Point, B: Point) {
  require(Point.linearIndependant(A, B))

  import Viewport._

  def translate(t: Point): Viewport = Viewport(origin + t, A, B)
  def right(movementFactor: Double = defaultMovementFactor): Viewport = translate(A * movementFactor)
  def left(movementFactor: Double = defaultMovementFactor): Viewport = translate(A * -movementFactor)
  def up(movementFactor: Double = defaultMovementFactor): Viewport = translate(B * -movementFactor)
  def down(movementFactor: Double = defaultMovementFactor): Viewport = translate(B * movementFactor)
  def focus(xRatio: Double, yRatio: Double): Viewport =
    translate(A * (xRatio - 0.5) + (B * (yRatio - 0.5)))

  def zoomOut(zx: Double = 0.5, zy: Double = 0.5): Viewport =
    Viewport(
      A = A * invFactorZoom,
      B = B * invFactorZoom,
      origin = origin + ((A * (-defaultZoomFactor * zx) + B * (-defaultZoomFactor * zy)) * invFactorZoom)
    )

  def zoomIn(zx: Double = 0.5, zy: Double = 0.5): Viewport =
    Viewport(
      A = A * (1 - defaultZoomFactor),
      B = B * (1 - defaultZoomFactor),
      origin = origin + (A * (defaultZoomFactor * zx)) + (B * (defaultZoomFactor * zy))
    )

  def zoom(zx: Double, zy: Double, steps: Int): Viewport =
    if (steps > 0)
      (0 until steps).foldLeft(this)((view, _) => view.zoomOut(zx, zy))
    else
      (0 until -steps).foldLeft(this)((view, _) => view.zoomIn(zx, zy))
}