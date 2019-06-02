package nutria.core.viewport

import nutria.core.Point
import nutria.core.viewport.Point.PointOps

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

    def rotAngle(p: Point): Point= {
      val c = Math.cos(angle)
      val s = Math.sin(angle)

      (c * p.x - s * p.y, s * p.x + c * p.y)
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

  def createByDefaultFocusAndLongs(ax: Long, ay: Long, bx: Long, by: Long): Viewport =
    createByFocus(Point(0.3, 0.1), Point(0.7, 0.3))(
      Point.createWithLongs(ax, ay), Point.createWithLongs(bx, by))

  val defaultMovementFactor: Double = 0.20
  val defaultZoomInFactor: Double = 0.60
}

case class Viewport(origin: Point, A: Point, B: Point) {
  import Viewport._

  def translate(t: Point): Viewport = Viewport(origin + t, A, B)
  def right(movementFactor: Double = defaultMovementFactor): Viewport = translate(A * movementFactor)
  def left(movementFactor: Double = defaultMovementFactor): Viewport = translate(A * -movementFactor)
  def up(movementFactor: Double = defaultMovementFactor): Viewport = translate(B * -movementFactor)
  def down(movementFactor: Double = defaultMovementFactor): Viewport = translate(B * movementFactor)
  def focus(xRatio: Double, yRatio: Double): Viewport =
    translate(A * (xRatio - 0.5) + (B * (yRatio - 0.5)))

  def zoom(z: Point, zoomFactor: Double): Viewport =
    Viewport(
      origin = origin + (A * z.x + B * z.y) * (1 - zoomFactor),
      A = A * zoomFactor,
      B = B * zoomFactor
    )

  def zoomOut(z: (Double, Double) = (0.5, 0.5), zoomFactor: Double = defaultZoomInFactor): Viewport =
    zoom(z, 1 / zoomFactor)
  def zoomIn(z: (Double, Double) = (0.5, 0.5), zoomFactor: Double = defaultZoomInFactor): Viewport =
    zoom(z, zoomFactor)
  def zoomSteps(z: (Double, Double) = (0.5, 0.5), steps: Int): Viewport =
    zoom(z, Math.pow(defaultZoomInFactor, steps))

  // scales up the viewport so that a) the center is unchanged b) the given aspect ratio is preserved.
  // see https://developer.mozilla.org/de/docs/Web/CSS/object-fit
  def cover(width: Double, height: Double): Viewport = {
    val lambda = (width * B.norm) / (height * A.norm)
    val mu = 1.0 / lambda
    if (lambda < 1) {
      Viewport(origin + A * (0.5 - lambda / 2), A * lambda, B)
    } else {
      Viewport(origin + B * (0.5 - mu / 2), A, B * mu)
    }
  }

  // scales down the viewport so that a) the center is unchanged b) the given aspect ratio is preserved.
  // see https://developer.mozilla.org/de/docs/Web/CSS/object-fit
  def contain(width: Double, height: Double): Viewport = {
    val lambda = (width * B.norm) / (height * A.norm)
    val mu = 1.0 / lambda
    if (lambda > 1) {
      Viewport(origin + A * (0.5 - lambda / 2), A * lambda, B)
    } else {
      Viewport(origin + B * (0.5 - mu / 2), A, B * mu)
    }
  }
}
