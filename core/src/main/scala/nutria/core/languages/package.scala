package nutria.core

import mathParser.complex.ComplexLanguage

package object languages {
  private def constructLang[V](vars: (String, V)*): ComplexLanguage[V] =
    mathParser.MathParser.complexLanguage
      .withVariables(vars.toList)

  implicit val lambda: ComplexLanguage[Lambda.type] = constructLang("lambda" -> Lambda)

  implicit val zAndLambda: ComplexLanguage[ZAndLambda] = constructLang("z" -> Z, "lambda" -> Lambda)

  implicit val zAndZDerAndLambda: ComplexLanguage[ZAndZDerAndLambda] = constructLang(
    "z"         -> Z,
    "z_derived" -> ZDer,
    "lambda"    -> Lambda
  )
}
