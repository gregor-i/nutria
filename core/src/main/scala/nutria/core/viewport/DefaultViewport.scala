package nutria.core.viewport

trait DefaultViewport {
  val defaultViewport: Viewport = Viewport(Point(-2.5, -1), Point(3.5, 0), Point(0, 2))
}

object DefaultViewport extends DefaultViewport