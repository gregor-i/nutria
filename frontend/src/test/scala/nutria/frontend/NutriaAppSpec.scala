package nutria.frontend

import nutria.core.{FractalEntity, FractalProgram, NewtonIteration}
import org.scalajs.dom
import org.scalatest.{FunSuite, Matchers}

class NutriaAppSpec extends FunSuite with Matchers{

  test("queryEncoded and queryDecoded") {
    val fractal = FractalEntity(program = NewtonIteration.default)
    val encoded = NutriaApp.queryEncoded(fractal)
    val decoded = NutriaApp.queryDecoded(encoded)
    decoded shouldBe Some(fractal)
  }
}
