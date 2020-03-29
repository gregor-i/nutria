package nutria.frontend.ui.explorer

import nutria.core.viewport.Point._
import nutria.core.viewport.Dimensions
import nutria.core.viewport
import nutria.core.{Point, Viewport}
import nutria.frontend.ui.explorer.Transform
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class TransformSpec extends AnyFunSuite with Matchers {
  def validate(moves: Seq[(Point, Point)]) = {
    val oldView = Viewport((-5, -5), (10, 0), (0, 10))
    val newView = Transform.applyToViewport(moves, oldView)

    val t1 = viewport.Transform(oldView, Dimensions(1, 1))
    val t2 = viewport.Transform(newView, Dimensions(1, 1))

    for (m <- moves) {
      val p1 = t1(m._1)
      val p2 = t2(m._2)
      assert(p1.x === p2.x +- 0.01)
      assert(p1.y === p2.y +- 0.01)
    }
  }

  test("applyToViewport: only translate") {
    val onlyTranslate = Seq(
      (0d, 0d)   -> (0.25d, 0.25),
      (0.5, 0.0) -> (0.75d, 0.25),
    )

    validate(onlyTranslate)
  }

  test("applyToViewport: only scale (zoom out)") {
    val onlyScale = Seq(
      (0d, 0d) -> (0.25, 0.25),
      (1d, 1d) -> (0.75, 0.75),
    )

    validate(onlyScale)
  }

  test("applyToViewport: only scale (zoom in)") {
    val onlyScale = Seq(
      (0.25, 0.25) -> (0d, 0d),
      (0.75, 0.75) -> (1d, 1d),
    )

    validate(onlyScale)
  }
}
