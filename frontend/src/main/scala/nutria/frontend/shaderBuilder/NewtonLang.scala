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
        case NewtonLang.initialLang.Times => s"product(vec2($left), vec2($right))"
        case NewtonLang.initialLang.Divided => s"divide(vec2($left), vec2($right))"
        case NewtonLang.functionLang.Plus => left + "+" + right
        case NewtonLang.functionLang.Minus => left + "-" + right
        case NewtonLang.functionLang.Times => s"product(vec2($left), vec2($right))"
        case NewtonLang.functionLang.Divided => s"divide(vec2($left), vec2($right))"
      },
      ifUnitary = (op, child) => op match {
        case NewtonLang.initialLang.Exp => s"exp(vec2($child).x) * vec2(cos($child).y, sin($child).y)"
        case NewtonLang.functionLang.Exp => s"exp(vec2($child).x) * vec2(cos($child).y, sin($child).y)"
      },
      ifVariable = varsToCode
    )
}
