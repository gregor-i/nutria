package nutria.frontend.shaderBuilder

import mathParser.algebra._
import spire.math.Complex

object NewtonLang {
  val functionLang = nutria.core.newton.Language.fLang
  val initialLang = nutria.core.newton.Language.c0Lang

  def toWebGlCode[V](node: SpireNode[Complex[Double], V], varsToCode: PartialFunction[V, String]): String =
    node.fold[String](
      ifConstant = c => Vec2(FloatLiteral(c.real.toFloat), FloatLiteral(c.imag.toFloat)).toCode,
      ifBinary = (op, left, right) => op match {
        case Plus => left + "+" + right
        case Minus => left + "-" + right
        case Times => s"complex_product(vec2($left), vec2($right))"
        case Divided => s"complex_divide(vec2($left), vec2($right))"
        case Power if right == "vec2(float(2), float(0))" => s"complex_sq(vec2($left))"
        case Power if right == "vec2(float(1), float(0))" => left
        case Power => println(right); ???
      },
      ifUnitary = (op, child) => op match {
        case Exp => s"complex_exp(vec2($child))"
      },
      ifVariable = varsToCode
    )
}
