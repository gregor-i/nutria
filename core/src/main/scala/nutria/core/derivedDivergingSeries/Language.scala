package nutria.core.derivedDivergingSeries

import mathParser.algebra.SpireLanguage
import spire.math.Complex

sealed trait ZAndZDerAndLambda
sealed trait ZAndLambda extends ZAndZDerAndLambda
case object Z extends ZAndLambda
case object Lambda extends ZAndLambda
case object ZDer extends ZAndZDerAndLambda

object Language {
  val initial: SpireLanguage[Complex[Double], Lambda.type] =
    mathParser.MathParser.complexLanguage
      .withVariables[Lambda.type](List('lambda -> Lambda))

  val iterationZ: SpireLanguage[Complex[Double], ZAndLambda] =
    mathParser.MathParser.complexLanguage
      .withVariables[ZAndLambda](List('z -> Z, 'lambda -> Lambda))

  val iterationZDer: SpireLanguage[Complex[Double], ZAndZDerAndLambda] =
    mathParser.MathParser.complexLanguage
      .withVariables[ZAndZDerAndLambda](List(
      'z -> Z,
      'lambda -> Lambda,
      Symbol("z'") -> ZDer
    ))
}

