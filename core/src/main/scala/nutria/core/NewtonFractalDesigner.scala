package nutria.core

import mathParser.complex.ComplexLanguage.syntax.doubleAsComplex
import mathParser.complex._
import nutria.core.languages.{StringFunction, ZAndLambda, _}

import scala.util.Random
import scala.util.chaining._

object NewtonFractalDesigner {

  def apply(constant: Complex, roots: Seq[Complex]): Option[FractalImage] = {
    val polynom =
      roots
        .pipe(coefficients)
        .pipe(integrate(_, constant))
        .pipe(stringify)

    StringFunction[ZAndLambda](polynom).map { stringFunction =>
      val parameter = NewtonFunctionParameter("f", value = stringFunction, includeDerivative = true)
      FractalTemplate
        .applyParameters(Examples.newtonIteration)(Vector(parameter))
        .pipe(FractalImage.fromTemplate)
    }
  }

  def animation(constant: Complex, roots: Seq[Complex], seed: Int): LazyList[FractalImage] = {
    val random = new Random(seed)

    val streams = roots.map { root =>
      streamPath(random.nextInt(), root)
    }

    LazyList
      .unfold(streams)(streams => Some((streams.map(_.head), streams.map(_.drop(1)))))
      .flatMap(apply(constant, _))
  }

  def streamPath(seed: Int, start: Complex): LazyList[Complex] = {
    val alpha  = 0.01
    val beta   = 0.01
    val gamma  = 0.99
    val random = new Random(seed)
    var d      = random.nextDouble() * 2.0 * Math.PI
    LazyList.iterate(start) { c =>
      d += Math.random() * alpha
      (c + (Complex(Math.sin(d), Math.cos(d)) * beta)) * gamma
    }
  }

  private implicit class EnrichComplex(a: Complex) {
    def +(b: Complex): Complex =
      ComplexEvaluate[Nothing].executeBinaryOperator(Plus, a, b)

    def *(b: Complex): Complex =
      ComplexEvaluate[Nothing].executeBinaryOperator(Times, a, b)

    def /(b: Complex): Complex =
      ComplexEvaluate[Nothing].executeBinaryOperator(Divided, a, b)

    def asString: String =
      f"(${a.real}%f + i * ${a.imag}%f)"
  }

  // https://en.wikipedia.org/wiki/Vieta%27s_formulas
  private def coefficients(roots: Seq[Complex]): Seq[Complex] = {
    val rootsMap: Int Map Complex = roots.zipWithIndex.map(_.swap).toMap
    (0 to roots.size)
      .map { i =>
        roots.indices
          .combinations(i)
          .map(_.map(rootsMap(_)))
          .map(_.fold(Complex(1, 0))(_ * _))
          .fold(Complex(0, 0))(_ + _)
      }
      .zipWithIndex
      .map {
        case (a, i) =>
          if (i % 2 == 0)
            a
          else
            a * (-1)
      }
  }

  private def integrate(coefficients: Seq[Complex], C: Complex): Seq[Complex] = {
    val n = coefficients.size
    val integrated =
      for ((a, i) <- coefficients.zipWithIndex)
        yield a / (n - i)
    integrated :+ C
  }

  private def stringify(coefficients: Seq[Complex]): String = {
    val n = coefficients.length
    (for ((a, i) <- coefficients.zipWithIndex)
      yield s"${a.asString}  * z ^ (${n - i - 1})")
      .mkString(" + ")
  }

}
