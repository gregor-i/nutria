package nutria.frontend.shaderBuilder

object NewtonLang {
  val functionLang = mathParser.MathParser.complexLanguage('x, 'lambda)
  val initialLang = mathParser.MathParser.complexLanguage('lambda)
}
