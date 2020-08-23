package nutria.shaderBuilder

import nutria.core.languages.{StringFunction, ZAndLambda}
import nutria.core.languages.zAndLambda
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import scala.util.chaining._

class OptimizerSpec extends AnyFunSuite with Matchers {
  private val generatedPolynom = (
    "(0.142857 + i * 0.000000)  * z ^ (7)"
      + " + (-0.013411 + i * 0.023875)  * z ^ (6)"
      + " + (-0.005533 + i * -0.009443)  * z ^ (5) "
      + " + (0.005760 + i * -0.000613)  * z ^ (4) "
      + " + (-0.001723 + i * 0.003456)  * z ^ (3) "
      + " + (-0.000387 + i * -0.003389)  * z ^ (2) "
      + " + (0.011165 + i * 0.011471)  * z ^ (1) "
      + " + (1.000000 + i * 0.000000)  * z ^ (0)"
  )

  private def stringFunction(string: String) =
    StringFunction[ZAndLambda](string)
      .getOrElse(cancel("string function could not be parsed"))

  test("the power optimizer removes all powers to natural numbers") {
    val webGlCode = stringFunction(generatedPolynom).node
      .pipe(PowerOptimizer.optimizer.optimize)
      .pipe(nutria.shaderBuilder.Function("some_name", _))

    assert("complex_power".r.findAllMatchIn(webGlCode).isEmpty)
    assert("\\+".r.findAllMatchIn(webGlCode).length == 7)
    assert("complex_product".r.findAllMatchIn(webGlCode).length == 13)

  }
}
