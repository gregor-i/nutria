package nutria.frontend.shaderBuilder

import mathParser.complex.ComplexLanguage

object NewtonLang {
  val functionLang = mathParser.MathParser.complexLanguage('x, 'lambda)
  val initialLang = mathParser.MathParser.complexLanguage('lambda)

  def toWebGlCode(node: ComplexLanguage#Node, varsToCode: PartialFunction[Symbol, String]): String =
    node.fold[String](
      ifConstant = c => Vec2(FloatLiteral(c.real.toFloat), FloatLiteral(c.imag.toFloat)).toCode,
      ifBinary = (op, left, right) => op match {
        case NewtonLang.initialLang.Plus => left + "+" + right
        case NewtonLang.initialLang.Minus => left + "-" + right
        case NewtonLang.initialLang.Times => s"complex_product(vec2($left), vec2($right))"
        case NewtonLang.initialLang.Divided => s"complex_divide(vec2($left), vec2($right))"
        case NewtonLang.functionLang.Plus => left + "+" + right
        case NewtonLang.functionLang.Minus => left + "-" + right
        case NewtonLang.functionLang.Times => s"complex_product(vec2($left), vec2($right))"
        case NewtonLang.functionLang.Divided => s"complex_divide(vec2($left), vec2($right))"
        case NewtonLang.functionLang.Power if right == "vec2(float(2), float(0))" => s"complex_sq(vec2($left))"
        case NewtonLang.functionLang.Power if right == "vec2(float(1), float(0))" => left
        case NewtonLang.functionLang.Power => println(right); ???
      },
      ifUnitary = (op, child) => op match {
        case NewtonLang.initialLang.Exp => s"complex_exp(vec2($child))"
        case NewtonLang.functionLang.Exp => s"complex_exp(vec2($child))"
      },
      ifVariable = varsToCode
    )
}
