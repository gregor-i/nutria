package nutria.data.consumers

import nutria.data.sequences.Mandelbrot

class MandelbrotSpec extends FunSuite with Matchers with Checkers {

  val iterations = 1000
  val escapeRadius = 2d

  private def sequence = Mandelbrot(iterations, escapeRadius)

  private def roughColoring = sequence andThen CountIterations()

  test("for all starting points inside the main cardioid, the sequence never ends") {
    check(forAll(chooseFromTheInsideOfTheCardioid) {
      c0 =>
        roughColoring(c0) shouldBe iterations
        true
    })
  }

  test("for all starting points inside the P2, the sequence never ends") {
    check(forAll(chooseFromPointsInsideOfP2) {
      c0 =>
        roughColoring(c0) shouldBe iterations
        true
    })
  }

  test("for the starting point (0,0) the sequence loops (0,0)") {
    val seq = sequence(0, 0).toList
    seq.length shouldBe iterations
    seq shouldBe List.fill(iterations)((0d, 0d))
  }

  test("after the first iteration, the sequence is at c0") {
    check(forAll(chooseFromUsefullStartPoints) {
      c0 =>
        val seq = sequence(c0)
        seq.next() shouldBe c0
        true
    })
  }

  test("for all starting points outside of the escape radius, the sequence must end directly after c0") {
    check(forAll(chooseFromPointsOutsideOfTheEscapeRadius) {
      c0 =>
        val seq = sequence(c0)
        seq.hasNext shouldBe true
        seq.next() shouldBe c0
        seq.hasNext shouldBe false
        true
    })
  }
}
