package nutria.core.viewport

import nutria.core.Point
import nutria.core.viewport.Point.PointOps

final case class Transform(view: Viewport, dimensions: Dimensions)
    extends HasDimensions
    with (Point => Point)
    with ((Double, Double) => Point) {

  private val scaleX       = view.A.x / width
  private val scaleY       = view.B.y / height
  private val shearX       = view.B.x / height
  private val shearY       = view.A.y / width
  private val translationX = view.origin.x
  private val translationY = view.origin.y

  private val factorInvert = 1d / (scaleX * scaleY - shearX * shearY)

  @inline def transformX(x: Double, y: Double): Double = scaleX * x + shearX * y + translationX
  @inline def transformY(x: Double, y: Double): Double = shearY * x + scaleY * y + translationY
  @inline def transform(x: Double, y: Double): (Double, Double) =
    (transformX(x, y), transformY(x, y))
  @inline override def apply(x: Double, y: Double): Point = transform(x, y)
  @inline override def apply(p: Point): Point             = transform(p._1, p._2)

  @inline def invertX(x: Double, y: Double): Double =
    (scaleY * (x - translationX) - shearX * (y - translationY)) * factorInvert
  @inline def invertY(x: Double, y: Double): Double =
    (scaleX * (y - translationY) - shearY * (x - translationX)) * factorInvert
  @inline def invert(x: Double, y: Double): Point = (invertX(x, y), invertY(x, y))
  @inline def invert(p: Point): Point             = (invertX(p._1, p._2), invertY(p._1, p._2))
}
