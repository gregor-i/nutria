package nutria.data.sequences

import spire.math.Complex

case class NewtonFractalByString(function: String, c0Function: String) extends Newton {
  type C = spire.math.Complex[Double]
  type F = (C, C) => C

  val fLang = mathParser.MathParser.complexLanguage('x, 'lambda)
  val c0Lang = mathParser.MathParser.complexLanguage('lambda)

  private def parseAndDerive(s: String): Option[(F, F)] =
    for {
      t1 <- fLang.parse(s)
      t2 = fLang.derive(t1)('x)
      f1 = (x: C, lambda: C) => fLang.evaluate(t1) { case 'x => x; case 'lambda => lambda }
      f2 = (x: C, lambda: C) => fLang.evaluate(t2) { case 'x => x; case 'lambda => lambda }
    } yield (f1, f2)

  private def parseC0Function(c0Function: String): Option[C => C] =
    c0Lang.parse(c0Function).map(t => lambda => c0Lang.evaluate(t) { case 'lambda => lambda })

  private val (f1, f2) = parseAndDerive(function).getOrElse(throw new RuntimeException("could not parse " + function))
  private val c0 = parseC0Function(c0Function).getOrElse(throw new RuntimeException("could not parse " + c0Function))

  override def c0(lambda: Complex[Double]): Complex[Double] = c0.apply(lambda)

  override def f(c: Complex[Double], lambda: Complex[Double]): Complex[Double] = f1(c, lambda)

  override def f_der(c: Complex[Double], lambda: Complex[Double]): Complex[Double] = f2(c, lambda)
}
