package nutria.core.divergingSeries

import mathParser.algebra.SpireLanguage
import spire.math.Complex

sealed trait ZAndLambda
case object Z extends ZAndLambda
case object Lambda extends ZAndLambda

object Language {
  val fLang: SpireLanguage[Complex[Double], ZAndLambda] =
    mathParser.MathParser.complexLanguage
      .withVariables[ZAndLambda](List('z -> Z, 'lambda -> Lambda))

  val c0Lang: SpireLanguage[Complex[Double], Lambda.type] =
    mathParser.MathParser.complexLanguage
      .withVariables[Lambda.type](List('lambda -> Lambda))
}

