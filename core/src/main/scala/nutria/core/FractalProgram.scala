package nutria.core

import eu.timepit.refined._
import eu.timepit.refined.api.Refined
import eu.timepit.refined.auto._
import eu.timepit.refined.boolean.And
import eu.timepit.refined.numeric.Interval.Open
import eu.timepit.refined.numeric.{NonNaN, Positive}
import io.circe.{Decoder, Encoder}
import monocle.Prism
import monocle.macros.GenPrism
import shapeless.Witness
import nutria.core.languages.{Lambda, StringFunction, XAndLambda, ZAndLambda, ZAndZDerAndLambda}

sealed trait FractalProgram

@monocle.macros.Lenses()
case class DivergingSeries(maxIterations: Int Refined Positive = refineMV(200),
                           escapeRadius: Double Refined (Positive And NonNaN) = refineMV(100),
                           initial: StringFunction[Lambda.type],
                           iteration: StringFunction[ZAndLambda]) extends FractalProgram

object DivergingSeries {
  def mandelbrot = DivergingSeries(
    initial = StringFunction.unsafe("0"),
    iteration = StringFunction.unsafe("z*z + lambda")
  )

  def juliaSet(c: Point) = DivergingSeries(
    initial = StringFunction.unsafe("lambda"),
    iteration = StringFunction.unsafe(s"z*z + (${c._1} + i*${c._2})"),
    maxIterations = 50
  )
}

@monocle.macros.Lenses()
case class DerivedDivergingSeries(maxIterations: Int Refined Positive = refineMV(200),
                                  escapeRadius: Double Refined (Positive And NonNaN) = refineMV(100),
                                  h2: Double Refined NonNaN = refineMV(2.0),
                                  angle: Double Refined Open[Witness.`0.0`.T, Witness.`6.28318530718`.T] = refineMV(0.78539816339), // todo: maybe define in degree? this 45°
                                  initialZ: StringFunction[Lambda.type],
                                  initialZDer: StringFunction[Lambda.type],
                                  iterationZ: StringFunction[ZAndLambda],
                                  iterationZDer: StringFunction[ZAndZDerAndLambda]) extends FractalProgram

object DerivedDivergingSeries{
  val mandelbrot = DerivedDivergingSeries(
    initialZ = StringFunction.unsafe("lambda"),
    initialZDer = StringFunction.unsafe("1"),
    iterationZ = StringFunction.unsafe("z*z + lambda"),
    iterationZDer = StringFunction.unsafe("z'*z*2 + 1")
  )

  def juliaSet(c: Point) = DerivedDivergingSeries(
    initialZ = StringFunction.unsafe("lambda"),
    initialZDer = StringFunction.unsafe("1"),
    iterationZ = StringFunction.unsafe(s"z*z + (${c._1} + i*${c._2})"),
    iterationZDer = StringFunction.unsafe("z'*z*2 + 1")
  )
}

@monocle.macros.Lenses()
case class NewtonIteration(maxIterations: Int Refined Positive = refineMV(200),
                           threshold: Double Refined (Positive And NonNaN) = refineMV(1e-4),
                           function: StringFunction[XAndLambda],
                           initial: StringFunction[Lambda.type],
                           center: Point = (0.0, 0.0),
                           brightnessFactor: Double Refined (Positive And NonNaN) = refineMV(25.0),
                           overshoot: Double Refined NonNaN = refineMV(1.0)
                          ) extends FractalProgram

object NewtonIteration {
  def mandelbrotPolynomial(n: Int): NewtonIteration = {
    def loop(n: Int): String =
      if (n == 1)
        "x"
      else
        s"(${loop(n - 1)})^2 + lambda"

    NewtonIteration(
      function = StringFunction.unsafe(loop(n)),
      initial = StringFunction.unsafe("lambda")
    )
  }

  val threeRoots = NewtonIteration(
    function = StringFunction.unsafe("x*x*x + 1"),
    initial = StringFunction.unsafe("lambda")
  )
}

@monocle.macros.Lenses()
case class FreestyleProgram(code: String) extends FractalProgram

object FreestyleProgram{
  val default = FreestyleProgram("color = vec4(abs(z.x), abs(z.y), length(z), 1.0);")
}

object FractalProgram extends CirceCodex {
  val newtonIteration: Prism[FractalProgram, NewtonIteration] = GenPrism[FractalProgram, NewtonIteration]
  val divergingSeries: Prism[FractalProgram, DivergingSeries] = GenPrism[FractalProgram, DivergingSeries]
  val derivedDivergingSeries: Prism[FractalProgram, DerivedDivergingSeries] = GenPrism[FractalProgram, DerivedDivergingSeries]
  val freestyleProgram: Prism[FractalProgram, FreestyleProgram] = GenPrism[FractalProgram, FreestyleProgram]

  implicit val ordering: Ordering[FractalProgram] = Ordering.by[FractalProgram, (Int, Int)] {
    case f: DivergingSeries => (1, f.iteration.hashCode)
    case f: DerivedDivergingSeries => (2, f.iterationZ.hashCode)
    case f: NewtonIteration => (3, f.function.string.length)
    case f: FreestyleProgram => (4, f.code.length)
  }

  implicit val decoder: Decoder[FractalProgram] = semiauto.deriveDecoder
  implicit val encoder: Encoder[FractalProgram] = semiauto.deriveEncoder
}