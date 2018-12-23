package nutria.core.viewport

import nutria.core.Point

object Point {
  private def longToDouble(l: Long): Double = java.lang.Double.longBitsToDouble(l)

  def createWithLongs(x: Long, y: Long):Point =
    (longToDouble(x), longToDouble(y))

  def apply(x:Double, y:Double): Point = (x, y)

  implicit class PointOps(val self:Point) extends AnyVal {
    @inline final def x:Double = self._1
    @inline final def y:Double = self._2

    @inline final def +(px: Double, py: Double): Point = (x + px, y + py)
    @inline final def -(px: Double, py: Double): Point = (x - px, y - py)

    @inline final def +(t: Point): Point = this + (t.x, t.y)
    @inline final def -(t: Point): Point = this - (t.x, t.y)

    @inline final def *(f: Double): Point = (x * f, y * f)
    @inline final def *(p: Point): Double = x * p.x + y * p.y

    @inline final def orth(): Point = (y, -x)
    @inline final def norm(): Double = Math.sqrt(self * self)
  }
}
