package ttd

import mathParser.MathParser
import mathParser.algebra.Times
import mathParser.implicits._
import nutria.frontend.shaderBuilder._
import org.scalatest._

class WebGlStatementSpec extends FunSuite with Matchers {
  object X

  val lang = MathParser.complexLanguage.withVariables(List("x" -> X))
  val varsToCode: PartialFunction[X.type, Ref[WebGlTypeVec2.type]] = {
    case X => Ref("x")
  }

  test("reuse binary declarations") {
    val node = lang.parse("x*x + x*x").get
    val statements = WebGlStatement.blockAssign(Ref("y"), node, varsToCode)

    statements(0).toCode shouldBe "vec2 local_var_1 = complex_product(vec2(x), vec2(x));"
    statements(1).toCode shouldBe "vec2 local_var_2 = local_var_1+local_var_1;"
  }


  test("reuse unitary declarations") {
    val node = lang.parse("sin(x) + sin(x)").get
    val statements = WebGlStatement.blockAssign(Ref("y"), node, varsToCode)

    statements(0).toCode shouldBe "vec2 local_var_1 = complex_sin(vec2(x));"
    statements(1).toCode shouldBe "vec2 local_var_2 = local_var_1+local_var_1;"
  }
}
