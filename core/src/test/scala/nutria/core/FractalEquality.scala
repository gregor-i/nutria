package nutria.core

import nutria.core.viewport.ViewportChooser.chooseFromUsefullStartPoints
import org.scalacheck.Prop
import org.scalacheck.Prop.forAll

object FractalEquality {
  def apply[A](left: Point => A, right: Point => A): Prop =
    forAll(chooseFromUsefullStartPoints) {
      p => left(p._1, p._2) == right(p._1, p._2)
    }
}
