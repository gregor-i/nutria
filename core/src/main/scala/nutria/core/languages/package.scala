package nutria.core

import mathParser.algebra.{SpireLanguage, SpireNode}
import spire.math.Complex

package object languages {
  type CLang[V] = SpireLanguage[Complex[Double], V]
  type CNode[V] = SpireNode[Complex[Double], V]

  private def constructLang[V](vars: (String, V)*): CLang[V] =
    mathParser.MathParser.complexLanguage
      .withVariables(vars.toList)

  implicit val lambda: CLang[Lambda.type] = constructLang("lambda" -> Lambda)

  implicit val zAndLambda: CLang[ZAndLambda] = constructLang("z" -> Z, "lambda" -> Lambda)

  implicit val zAndZDerAndLambda: CLang[ZAndZDerAndLambda] = constructLang(
    "z"         -> Z,
    "z_derived" -> ZDer,
    "lambda"    -> Lambda
  )
}
