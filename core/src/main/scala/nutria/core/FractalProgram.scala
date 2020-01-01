package nutria.core

import eu.timepit.refined.refineMV
import eu.timepit.refined.api.Refined
import eu.timepit.refined.boolean.And
import eu.timepit.refined.numeric.Interval.Open
import eu.timepit.refined.numeric.{NonNaN, Positive}
import io.circe.Codec
import monocle.Prism
import monocle.macros.GenPrism
import nutria.core.languages.{Lambda, StringFunction, XAndLambda, ZAndLambda, ZAndZDerAndLambda}
import shapeless.Witness

sealed trait FractalProgram

@monocle.macros.Lenses()
case class DivergingSeries(
    maxIterations: Int Refined Positive = refineMV(200),
    escapeRadius: Double Refined Positive = refineMV(100.0),
    initial: StringFunction[Lambda.type],
    iteration: StringFunction[ZAndLambda],
    colorInside: RGBA = RGBA.white,
    colorOutside: RGBA = RGBA.black
) extends FractalProgram

object DivergingSeries {
  def default = DivergingSeries(
    initial = StringFunction.unsafe("0"),
    iteration = StringFunction.unsafe("z*z + lambda")
  )

}

@monocle.macros.Lenses()
case class DerivedDivergingSeries(
    maxIterations: Int Refined Positive = refineMV(200),
    escapeRadius: Double Refined Positive = refineMV(100.0),
    h2: Double Refined NonNaN = refineMV(2.0),
    angle: Double Refined Open[Witness.`0.0`.T, Witness.`6.28318530718`.T] = refineMV(0.78539816339), // todo: maybe define in degree? this is 45°
    initialZ: StringFunction[Lambda.type],
    initialZDer: StringFunction[Lambda.type],
    iterationZ: StringFunction[ZAndLambda],
    iterationZDer: StringFunction[ZAndZDerAndLambda],
    colorInside: RGBA = RGBA(0.0, 0.0, 255.0 / 4.0),
    colorLight: RGBA = RGBA.white,
    colorShadow: RGBA = RGBA.black
) extends FractalProgram

object DerivedDivergingSeries {
  val default = DerivedDivergingSeries(
    initialZ = StringFunction.unsafe("lambda"),
    initialZDer = StringFunction.unsafe("1"),
    iterationZ = StringFunction.unsafe("z*z + lambda"),
    iterationZDer = StringFunction.unsafe("z'*z*2 + 1")
  )
}

@monocle.macros.Lenses()
case class NewtonIteration(
    maxIterations: Int Refined Positive = refineMV(200),
    threshold: Double Refined Positive = refineMV(1e-4),
    function: StringFunction[XAndLambda],
    initial: StringFunction[Lambda.type],
    center: Point = (0.0, 0.0),
    brightnessFactor: Double Refined Positive = refineMV(25.0),
    overshoot: Double Refined NonNaN = refineMV(1.0)
) extends FractalProgram

object NewtonIteration {
  val default = NewtonIteration(
    function = StringFunction.unsafe("x*x*x + 1"),
    initial = StringFunction.unsafe("lambda")
  )
}

@monocle.macros.Lenses()
case class FreestyleProgram(code: String, parameters: Seq[Parameter] = Seq.empty)
    extends FractalProgram

object FreestyleProgram {
  val default = FreestyleProgram("color = vec4(abs(z.x), abs(z.y), length(z), 1.0);")

  val allVaribalesRegex = "\\$\\{([\\w\\d^\\}]+)\\}".r

  def variables(code: String): Seq[String] =
    allVaribalesRegex
      .findAllMatchIn(code)
      .map(_.group(1))
      .distinct
      .toSeq
      .sorted
}

object FractalProgram extends CirceCodex {
  val newtonIteration: Prism[FractalProgram, NewtonIteration] =
    GenPrism[FractalProgram, NewtonIteration]
  val divergingSeries: Prism[FractalProgram, DivergingSeries] =
    GenPrism[FractalProgram, DivergingSeries]
  val derivedDivergingSeries: Prism[FractalProgram, DerivedDivergingSeries] =
    GenPrism[FractalProgram, DerivedDivergingSeries]
  val freestyleProgram: Prism[FractalProgram, FreestyleProgram] =
    GenPrism[FractalProgram, FreestyleProgram]

  implicit val ordering: Ordering[FractalProgram] = Ordering.by[FractalProgram, (Int, Int)] {
    case f: DivergingSeries        => (1, f.iteration.hashCode)
    case f: DerivedDivergingSeries => (2, f.iterationZ.hashCode)
    case f: NewtonIteration        => (3, f.function.hashCode)
    case f: FreestyleProgram       => (4, f.code.hashCode)
  }

  implicit val codec: Codec[FractalProgram] = semiauto.deriveConfiguredCodec
}
