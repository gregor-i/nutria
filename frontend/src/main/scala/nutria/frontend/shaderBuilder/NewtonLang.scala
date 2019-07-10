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
        case Power => s"complex_power($left, $right)"
      },
      ifUnitary = (op, child) => op match {
        case Neg => s"-($child)"
        case Sin => s"complex_sin(vec2($child))"
        case Cos => s"complex_cos(vec2($child))"
        case Tan => s"complex_tan(vec2($child))"
        case Asin => ???
        case Acos => ???
        case Atan => ???
        case Sinh => ???
        case Cosh => ???
        case Tanh => ???
        case Exp => s"complex_exp(vec2($child))"
        case Log => s"complex_log(vec2($child))"
      },
      ifVariable = varsToCode
    )
}
