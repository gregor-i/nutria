package nutria.frontend.util

import org.scalatest.funsuite.AnyFunSuite

class UpdatableSpec extends AnyFunSuite {
  test("construction and basis functionality") {
    var x: Int    = 123
    val updatable = Updatable[Int, Int](x, x = _)

    assert(updatable.state == 123)
    updatable.update(234)
    assert(x == 234)
  }

  test("polymorphic usage") {
    var x: Double = 123.0
    val updatable = Updatable[Double, Int](x, x = _)

    assert(updatable.state == 123.0)
    updatable.update(234)
    assert(x == 234.0)
  }

  test("compose a lens") {
    var x: (Int, Boolean) = (123, true)
    val updatable: Updatable[Int, Int] = Updatable.composeLens(
      Updatable[(Int, Boolean), (Int, Boolean)](x, x = _),
      monocle.function.Field1.tuple2Field1[Int, Boolean].first
    )

    assert(updatable.state == 123)
    updatable.update(234)
    assert(x == (234, true))
  }

  test("compose a polymorphic lens") {
    var x: (Int, Boolean) = (123, true)
    val updatable: Updatable[Int, Int] = Updatable.composeLens(
      Updatable[(Int, Boolean), (Int, Boolean)](x, x = _),
      monocle.function.Field1.tuple2Field1[Int, Boolean].first
    )

    assert(updatable.state == 123)
    updatable.update(234)
    assert(x == (234, true))
  }
}
