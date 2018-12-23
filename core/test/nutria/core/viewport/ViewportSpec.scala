package nutria.core.viewport

import nutria.core.viewport.Point.PointOps
import org.scalacheck.Gen.choose
import org.scalacheck.Prop.forAll
import org.scalatest.{FunSuite, Matchers}
import org.scalatest.prop.Checkers
import nutria.core.syntax._

class ViewportSpec extends FunSuite with Matchers with Checkers {
  //  def is = s2"""
  //        A viewports describes a part of the complex plane.
  //        The part is a parallelogram and the corners are:
  //          Origin
  //          Origin + A
  //          Origin + A + B
  //          Origin + B
  //
  //        The Viewports can be moved and zoomed:
  //          move has invers operations $moveHasInversOperations
  //          zoom has invers operations $zoomHasInversOperations
  //          focused to a point         $focused
  //      """

  def beClose(left: Viewport, right: Viewport) = {
    left.origin._1 shouldBe (right.origin._1 +- 0.01)
    left.origin._2 shouldBe (right.origin._2 +- 0.01)
    left.A._1 shouldBe (right.A._1 +- 0.01)
    left.A._2 shouldBe (right.A._2 +- 0.01)
    left.B._1 shouldBe (right.B._1 +- 0.01)
    left.B._2 shouldBe (right.B._2 +- 0.01)
  }

  test("Viewport should have invertible move Operations") {
    check(forAll(ViewportChooser.chooseViewport, choose(-10d, 10d)) {
      (viewport, factor) =>
        beClose(viewport.right(factor).left(factor), viewport)
        beClose(viewport.left(factor).right(factor), viewport)
        beClose(viewport.up(factor).down(factor), viewport)
        beClose(viewport.down(factor).up(factor), viewport)
        true
    })
  }

  test("Viewport should have invertible zoom Operations") {
    check(forAll(ViewportChooser.chooseViewport, choose(-10d, 10d), choose(0d, 1d), choose(0d, 1d)) {
      (viewport, factor, zoomX, zoomY) =>
        beClose(viewport.zoomIn((zoomX, zoomY)).zoomOut((zoomX, zoomY)), viewport)
        beClose(viewport.zoomOut((zoomX, zoomY)).zoomIn((zoomX, zoomY)), viewport)
        true
    })
  }
}
