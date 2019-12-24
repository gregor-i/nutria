package nutria.core.viewport

import org.scalacheck.Gen.choose
import org.scalacheck.Prop.{forAll, _}
import org.scalatest.Assertion
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class ViewportSpec extends AnyFunSuite with Matchers {
  def beClose(left: Viewport, right: Viewport): Assertion = {
    left.origin._1 shouldBe (right.origin._1 +- 0.01)
    left.origin._2 shouldBe (right.origin._2 +- 0.01)
    left.A._1 shouldBe (right.A._1 +- 0.01)
    left.A._2 shouldBe (right.A._2 +- 0.01)
    left.B._1 shouldBe (right.B._1 +- 0.01)
    left.B._2 shouldBe (right.B._2 +- 0.01)
  }

  test("Viewport should have invertible move Operations") {
    forAll(ViewportChooser.chooseViewport, choose(-10d, 10d)) { (viewport, factor) =>
      beClose(viewport.right(factor).left(factor), viewport)
      beClose(viewport.left(factor).right(factor), viewport)
      beClose(viewport.up(factor).down(factor), viewport)
      beClose(viewport.down(factor).up(factor), viewport)
      true
    }
  }

  test("Viewport should have invertible zoom Operations") {
    forAll(ViewportChooser.chooseViewport, choose(-10d, 10d), choose(0d, 1d), choose(0d, 1d)) {
      (viewport, factor, zoomX, zoomY) =>
        beClose(viewport.zoomIn((zoomX, zoomY)).zoomOut((zoomX, zoomY)), viewport)
        beClose(viewport.zoomOut((zoomX, zoomY)).zoomIn((zoomX, zoomY)), viewport)
        true
    }
  }
}
