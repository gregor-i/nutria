package nutria.frontend

import nutria.core.{FractalEntity, NewtonIteration}
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class NutriaAppSpec extends AnyFunSuite with Matchers {

  test("queryEncoded and queryDecoded") {
    val fractal = FractalEntity(program = NewtonIteration.default)
    val encoded = Router.queryEncoded(fractal)
    val decoded = Router.queryDecoded(encoded)
    decoded shouldBe Some(fractal)
  }
}
