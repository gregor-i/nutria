package nutria.shaderBuilder

import mathParser.MathParser
import mathParser.Implicits._
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class WebGlStatementSpec extends AnyFunSuite with Matchers {
  object X

  val lang = MathParser.complexLanguage.withVariables(List("x" -> X))

  test("reuse binary declarations") {
    val node       = lang.parse("x*x + x*x").get
    val statements = nutria.shaderBuilder.Function("f1", node)(lang)

    statements should include("vec2 var_1 = complex_product(x, x);")
    statements should include("vec2 var_2 = var_1 + var_1;")
  }

  test("reuse unitary declarations") {
    val node       = lang.parse("sin(x) + sin(x)").get
    val statements = nutria.shaderBuilder.Function("f1", node)(lang)

    statements should include("vec2 var_1 = complex_sin(x);")
    statements should include("vec2 var_2 = var_1 + var_1;")
  }
}
