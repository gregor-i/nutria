package nutria.core

import io.circe.Codec
import monocle.Lens
import monocle.macros.{GenLens, GenPrism, Lenses}
import nutria.CirceCodec
import nutria.core.languages.{Lambda, StringFunction, ZAndLambda}

sealed trait Parameter {
  def name: String
}

@Lenses
case class IntParameter(name: String, value: Int) extends Parameter
@Lenses
case class FloatParameter(name: String, value: Double) extends Parameter
@Lenses
case class RGBAParameter(name: String, value: RGBA) extends Parameter
@Lenses
case class ColorGradientParameter(name: String, value: Seq[RGBA]) extends Parameter
@Lenses
case class FunctionParameter(name: String, value: StringFunction[ZAndLambda], includeDerivative: Boolean = false) extends Parameter
@Lenses
case class InitialFunctionParameter(name: String, value: StringFunction[Lambda.type], includeDerivative: Boolean = false) extends Parameter
@Lenses
case class NewtonFunctionParameter(name: String, value: StringFunction[ZAndLambda], includeDerivative: Boolean = false) extends Parameter

object Parameter extends CirceCodec {

  val name = Lens[Parameter, String](get = _.name) { newName =>
    {
      case p: IntParameter             => IntParameter.name.set(newName)(p)
      case p: FloatParameter           => FloatParameter.name.set(newName)(p)
      case p: RGBAParameter            => RGBAParameter.name.set(newName)(p)
      case p: ColorGradientParameter   => ColorGradientParameter.name.set(newName)(p)
      case p: FunctionParameter        => FunctionParameter.name.set(newName)(p)
      case p: InitialFunctionParameter => InitialFunctionParameter.name.set(newName)(p)
      case p: NewtonFunctionParameter  => NewtonFunctionParameter.name.set(newName)(p)
    }
  }

  val prismIntParameter             = GenPrism[Parameter, IntParameter]
  val prismFloatParameter           = GenPrism[Parameter, FloatParameter]
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

  implicit val codec: Codec[Parameter] = semiauto.deriveConfiguredCodec
}
