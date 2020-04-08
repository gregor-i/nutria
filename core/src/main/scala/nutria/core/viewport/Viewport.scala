package nutria.core.viewport

import io.circe.{Codec, Decoder, Encoder}
import nutria.core.viewport.Point.PointOps
import nutria.core.{CirceCodec, Point}

object Viewport extends CirceCodec {
  val mandelbrot: Viewport = Viewport(Point(-2.5, -1), Point(3.5, 0), Point(0, 2))
  val aroundZero: Viewport = Viewport(Point(-2.0, -2.0), Point(4.0, 0), Point(0, 4.0))

  val defaultViewport: Viewport = mandelbrot

  implicit val codec: Codec[Viewport] = Codec.from(
    encodeA = Encoder[Vector[Double]].contramap(
      view => Vector(view.origin._1, view.origin._2, view.A._1, view.A._2, view.B._1, view.B._2)
    ),
    decodeA = Decoder[Vector[Double]].emap {
      case Vector(ox, oy, ax, ay, bx, by) => Right(Viewport((ox, oy), (ax, ay), (bx, by)))
      case _                              => Left("no match")
    }
  )

  val defaultMovementFactor: Double = 0.20
  val defaultZoomInFactor: Double   = 0.60
}

case class Viewport(origin: Point, A: Point, B: Point) {

  import Viewport._

  def translate(t: Point): Viewport = Viewport(origin + t, A, B)
  def right(movementFactor: Double = defaultMovementFactor): Viewport =
    translate(A * movementFactor)
  def left(movementFactor: Double = defaultMovementFactor): Viewport =
    translate(A * -movementFactor)
  def up(movementFactor: Double = defaultMovementFactor): Viewport   = translate(B * -movementFactor)
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
  def zoomSteps(z: (Double, Double) = (0.5, 0.5), steps: Double): Viewport =
    zoom(z, Math.pow(defaultZoomInFactor, steps))

  def rotate(z: (Double, Double) = (0.5, 0.5), angle: Double): Viewport = {
    val A_rot = A.rotate(angle)
    val B_rot = B.rotate(angle)
    Viewport(
      origin = origin + (A - A_rot) * z.x + (B - B_rot) * z.y,
      A = A_rot,
      B = B_rot
    )
  }

  // scales up the viewport so that a) the center is unchanged b) the given aspect ratio is preserved.
  // see https://developer.mozilla.org/de/docs/Web/CSS/object-fit
  def cover(width: Double, height: Double): Viewport = {
    val lambda = (width * B.abs) / (height * A.abs)
    val mu     = 1.0 / lambda
    if (lambda < 1) {
      Viewport(origin + A * (0.5 - lambda / 2), A * lambda, B)
    } else {
      Viewport(origin + B * (0.5 - mu / 2), A, B * mu)
    }
  }

  // scales down the viewport so that a) the center is unchanged b) the given aspect ratio is preserved.
  // see https://developer.mozilla.org/de/docs/Web/CSS/object-fit
  def contain(width: Double, height: Double): Viewport = {
    val lambda = (width * B.abs) / (height * A.abs)
    val mu     = 1.0 / lambda
    if (lambda > 1) {
      Viewport(origin + A * (0.5 - lambda / 2), A * lambda, B)
    } else {
      Viewport(origin + B * (0.5 - mu / 2), A, B * mu)
    }
  }

  def flipB: Viewport =
    Viewport(origin + B, A, B * -1.0)
}
