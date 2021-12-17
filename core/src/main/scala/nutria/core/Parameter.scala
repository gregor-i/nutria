package nutria.core

import io.circe.Codec
import mathParser.complex.Complex
import monocle.Lens
import monocle.macros.{GenLens, GenPrism, Lenses}
import nutria.CirceCodec
import nutria.core.languages.{Lambda, StringFunction, ZAndLambda}

sealed trait Parameter {
  def name: String
  def description: String
}

@Lenses
case class IntParameter(name: String, description: String = "", value: Int) extends Parameter
@Lenses
case class FloatParameter(name: String, description: String = "", value: Double) extends Parameter
@Lenses
case class ComplexParameter(name: String, description: String = "", value: Complex) extends Parameter
@Lenses
case class RGBAParameter(name: String, description: String = "", value: RGBA) extends Parameter
@Lenses
case class ColorGradientParameter(name: String, description: String = "", value: Seq[RGBA]) extends Parameter
@Lenses
case class FunctionParameter(
    name: String,
    description: String = "",
    value: StringFunction[ZAndLambda],
    includeDerivative: Boolean = false
) extends Parameter
@Lenses
case class InitialFunctionParameter(
    name: String,
    description: String = "",
    value: StringFunction[Lambda.type],
    includeDerivative: Boolean = false
) extends Parameter
@Lenses
case class NewtonFunctionParameter(
    name: String,
    description: String = "",
    value: StringFunction[ZAndLambda],
    includeDerivative: Boolean = false
) extends Parameter

object Parameter extends CirceCodec {
  val name = Lens[Parameter, String](get = _.name) { newName =>
    {
      case p: IntParameter             => IntParameter.name.set(newName)(p)
      case p: FloatParameter           => FloatParameter.name.set(newName)(p)
      case p: ComplexParameter         => ComplexParameter.name.set(newName)(p)
      case p: RGBAParameter            => RGBAParameter.name.set(newName)(p)
      case p: ColorGradientParameter   => ColorGradientParameter.name.set(newName)(p)
      case p: FunctionParameter        => FunctionParameter.name.set(newName)(p)
      case p: InitialFunctionParameter => InitialFunctionParameter.name.set(newName)(p)
      case p: NewtonFunctionParameter  => NewtonFunctionParameter.name.set(newName)(p)
    }
  }

  val description = Lens[Parameter, String](get = _.description) { newDescription =>
    {
      case p: IntParameter             => IntParameter.description.set(newDescription)(p)
      case p: FloatParameter           => FloatParameter.description.set(newDescription)(p)
      case p: ComplexParameter         => ComplexParameter.description.set(newDescription)(p)
      case p: RGBAParameter            => RGBAParameter.description.set(newDescription)(p)
      case p: ColorGradientParameter   => ColorGradientParameter.description.set(newDescription)(p)
      case p: FunctionParameter        => FunctionParameter.description.set(newDescription)(p)
      case p: InitialFunctionParameter => InitialFunctionParameter.description.set(newDescription)(p)
      case p: NewtonFunctionParameter  => NewtonFunctionParameter.description.set(newDescription)(p)
    }
  }

  val prismIntParameter             = GenPrism[Parameter, IntParameter]
  val prismFloatParameter           = GenPrism[Parameter, FloatParameter]
  val prismComplexParameter         = GenPrism[Parameter, ComplexParameter]
  val prismRGBAParameter            = GenPrism[Parameter, RGBAParameter]
  val prismColorGradientParameter   = GenPrism[Parameter, ColorGradientParameter]
  val prismFunctionParameter        = GenPrism[Parameter, FunctionParameter]
  val prismInitialFunctionParameter = GenPrism[Parameter, InitialFunctionParameter]
  val prismNewtonFunctionParameter  = GenPrism[Parameter, NewtonFunctionParameter]

  def setParameters(parameters: Vector[Parameter], newParameters: Vector[Parameter]): Vector[Parameter] = {
    (parameters ++ newParameters).reverse.distinctBy(_.name).reverse
  }

  def setParameter(list: Vector[Parameter], newParameter: Parameter): Vector[Parameter] =
    list.indexWhere(_.name == newParameter.name) match {
      case -1    => list.appended(newParameter)
      case index => list.updated(index, newParameter)
    }

  implicit val complexCodec: Codec[Complex] = semiauto.deriveConfiguredCodec
  implicit val codec: Codec[Parameter]      = semiauto.deriveConfiguredCodec

  implicit val ordering: Ordering[Parameter] = Ordering.by {
    case p: IntParameter             => (1, p.name)
    case p: FloatParameter           => (2, p.name)
    case p: ComplexParameter         => (3, p.name)
    case p: RGBAParameter            => (4, p.name)
    case p: ColorGradientParameter   => (5, p.name)
    case p: FunctionParameter        => (6, p.name)
    case p: InitialFunctionParameter => (7, p.name)
    case p: NewtonFunctionParameter  => (8, p.name)
  }
}
