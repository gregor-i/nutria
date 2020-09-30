package nutria.frontend.util

import org.scalatest.funsuite.AnyFunSuite

class UpdatableSpec extends AnyFunSuite {
  test("construction and basis functionality") {
    var x: Int    = 123
    val updatable = Updatable[Int](x, x = _)

    assert(updatable.state == 123)
    updatable.update(234)
    assert(x == 234)
  }

  test("polymorphic usage") {
    var x: Double = 123.0
    val updatable = Updatable.polymorphic[Double, Int](x, x = _)

    assert(updatable.state == 123.0)
    updatable.update(234)
    assert(x == 234.0)
  }

  test("apply a lens") {}
}
