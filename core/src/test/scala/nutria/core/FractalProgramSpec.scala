package nutria.core

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class FractalProgramSpec extends AnyFunSuite with Matchers {
  test("freestyle variables extractation: example 1") {
    val code = """${V1} ${V2} ${V2} ${VV}"""
    FreestyleProgram.variables(code) shouldBe Seq("V1", "V2", "VV")
  }

}
