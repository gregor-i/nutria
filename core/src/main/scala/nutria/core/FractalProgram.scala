package nutria.core

import eu.timepit.refined.api.Refined
import eu.timepit.refined.numeric.{NonNaN, Positive}
import io.circe.Codec
import mathParser.algebra.SpireNode
import monocle.Prism
import monocle.macros.GenPrism
import nutria.core.languages.{Lambda, StringFunction, XAndLambda, ZAndLambda, ZAndZDerAndLambda}
import spire.math.Complex

sealed trait FractalProgram

@monocle.macros.Lenses()
case class DivergingSeries(
    maxIterations: Int Refined Positive = refineUnsafe(200),
    escapeRadius: Double Refined Positive = refineUnsafe(100.0),
    initial: StringFunction[Lambda.type],
    iteration: StringFunction[ZAndLambda],
    coloring: DivergingSeriesColoring = TimeEscape()
) extends FractalProgram

object DivergingSeries {
  def default = DivergingSeries(
    initial = StringFunction.unsafe("0"),
    iteration = StringFunction.unsafe("z*z + lambda")
  )

  def deriveIteration(series: DivergingSeries): SpireNode[Complex[Double], ZAndZDerAndLambda] = {
    import mathParser.algebra._
    import mathParser.{BinaryNode, ConstantNode, UnitaryNode, VariableNode}

    type C = Complex[Double]
    type V = nutria.core.languages.ZAndZDerAndLambda

    import mathParser.algebra.SpireLanguage.syntax._
    import nutria.core.languages._
    import spire.implicits._

    // todo: this is a copy of mathparser.derive with few changes ...
    def derive(term: SpireNode[C, V]): SpireNode[C, V] =
      term match {
        case VariableNode(Lambda)              => one[C, V]
        case VariableNode(Z)                   => VariableNode(ZDer)
        case VariableNode(ZDer)                => throw new IllegalArgumentException()
        case VariableNode(_) | ConstantNode(_) => zero[C, V]
        case UnitaryNode(op, f) =>
          op match {
            case Neg  => neg(derive(f))
            case Sin  => derive(f) * cos(f)
            case Cos  => neg(derive(f) * sin(f))
            case Tan  => derive(f) / (cos(f) * cos(f))
            case Asin => derive(f) / sqrt(one[C, V] - (f * f))
            case Acos => neg(derive(f)) / sqrt(one[C, V] - (f * f))
            case Atan => derive(f) / (one[C, V] + (f * f))
            case Sinh => derive(f) * cosh(f)
            case Cosh => derive(f) * sinh(f)
            case Tanh => derive(f) / (cosh(f) * cosh(f))
            case Exp  => derive(f) * exp(f)
            case Log  => derive(f) / f
          }
        case BinaryNode(op, f, g) =>
          op match {
            case Plus    => derive(f) + derive(g)
            case Minus   => derive(f) - derive(g)
            case Times   => (derive(f) * g) + (derive(g) * f)
            case Divided => ((derive(g) * f) - (derive(f) * g)) / (g * g)
            case Power   => (derive(f) * g * (f ^ (g - one[C, V]))) + (derive(g) * f * log(f))
          }
      }

    derive(series.iteration.node.asInstanceOf[SpireNode[C, V]])
  }
}

@monocle.macros.Lenses()
case class NewtonIteration(
    maxIterations: Int Refined Positive = refineUnsafe(200),
    threshold: Double Refined Positive = refineUnsafe(1e-4),
    function: StringFunction[XAndLambda],
    initial: StringFunction[Lambda.type],
    center: Point = (0.0, 0.0),
    brightnessFactor: Double Refined Positive = refineUnsafe(25.0),
    overshoot: Double Refined NonNaN = refineUnsafe(1.0)
) extends FractalProgram

object NewtonIteration {
  val default = NewtonIteration(
    function = StringFunction.unsafe("x^3 + 1"),
    initial = StringFunction.unsafe("lambda")
  )
}

@monocle.macros.Lenses()
case class FreestyleProgram(code: String, parameters: Seq[Parameter] = Seq.empty) extends FractalProgram

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

object FractalProgram extends CirceCodec {
  val newtonIteration: Prism[FractalProgram, NewtonIteration] =
    GenPrism[FractalProgram, NewtonIteration]
  val divergingSeries: Prism[FractalProgram, DivergingSeries] =
    GenPrism[FractalProgram, DivergingSeries]
  val freestyleProgram: Prism[FractalProgram, FreestyleProgram] =
    GenPrism[FractalProgram, FreestyleProgram]

  implicit val ordering: Ordering[FractalProgram] = Ordering.by[FractalProgram, (Int, Int)] {
    case f: DivergingSeries  => (1, f.iteration.hashCode)
    case f: NewtonIteration  => (3, f.function.hashCode)
    case f: FreestyleProgram => (4, f.code.hashCode)
  }

  implicit val codec: Codec[FractalProgram] = semiauto.deriveConfiguredCodec
}
