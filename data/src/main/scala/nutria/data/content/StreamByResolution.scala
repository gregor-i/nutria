package nutria.data.content

import nutria.core.{Dimensions, Point, Viewport}
import nutria.data.syntax._

object StreamByResolution {
  def apply[A](view:Viewport, startResolution: Dimensions, steps: Int, function: Point => A): Stream[CachedContent[A]] = {
    val initial = view.withDimensions(startResolution).withContent(function).cached

    def next(prev: CachedContent[A]):CachedContent[A] = {
      val dim = prev.dimensions.scale(2)
      val trans = view.withDimensions(dim)
      new Content[A] {
        override val dimensions: Dimensions = dim
        override def apply(x: Int, y: Int): A =
          if (x % 2 == 0 && y % 2 == 0)
            prev(x / 2, y / 2)
          else
            function(trans(x, y))
      }.cached
    }

    def stream(img:CachedContent[A]): Stream[CachedContent[A]] = img #:: stream(next(img))

    stream(initial).take(steps)
  }
}
