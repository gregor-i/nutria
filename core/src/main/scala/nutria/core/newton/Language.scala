package nutria.core.newton

import mathParser.algebra.SpireLanguage
import spire.math.Complex

sealed trait XAndLambda
case object X extends XAndLambda
case object Lambda extends XAndLambda

object Language {
  val fLang: SpireLanguage[Complex[Double], XAndLambda] =
    mathParser.MathParser.complexLanguage
    .withVariables[XAndLambda](List('x -> X, 'lambda -> Lambda))

  val c0Lang: SpireLanguage[Complex[Double], Lambda.type] =
    mathParser.MathParser.complexLanguage
    .withVariables[Lambda.type](List('lambda -> Lambda))
}
