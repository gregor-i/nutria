package nutria.data.sequences

import spire.math.Complex
import nutria.core.newton._
import mathParser.implicits._

case class NewtonFractalByString(function: String, c0Function: String) extends Newton {
  type C = spire.math.Complex[Double]
  type F = (C, C) => C

  private def parseAndDerive(s: String): Option[(F, F)] =
    for {
      t1 <- Language.fLang.parse(s)
      t2 = Language.fLang.derive(t1)(X)
      f1 = (x:C, lambda:C) => Language.fLang.evaluate(t1){case X => x; case Lambda => lambda}
      f2 = (x:C, lambda:C) => Language.fLang.evaluate(t2){case X => x; case Lambda => lambda}
    } yield (f1, f2)

  private def parseC0Function(c0Function: String): Option[C => C] =
    Language.c0Lang.parse(c0Function).map(t => lambda => Language.c0Lang.evaluate(t){case Lambda => lambda})

  private val (f1, f2) = parseAndDerive(function).getOrElse(throw new RuntimeException("could not parse " + function))
  private val c0 = parseC0Function(c0Function).getOrElse(throw new RuntimeException("could not parse " + c0Function))

  override def c0(lambda: Complex[Double]): Complex[Double] = c0.apply(lambda)

  override def f(c: Complex[Double], lambda: Complex[Double]): Complex[Double] = f1(c, lambda)

  override def f_der(c: Complex[Double], lambda: Complex[Double]): Complex[Double] = f2(c, lambda)
}
