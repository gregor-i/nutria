package nutria.frontend.shaderBuilder

import mathParser.algebra._
import spire.math.Complex

object DivergingSeriesLang {
  val functionLang = nutria.core.divergingSeries.Language.fLang
  val initialLang = nutria.core.divergingSeries.Language.c0Lang

  // currently identical
  def toWebGlCode[V](node: SpireNode[Complex[Double], V], varsToCode: PartialFunction[V, String]): String =
    nutria.frontend.shaderBuilder.NewtonLang.toWebGlCode(node, varsToCode)
}
