package nutria.core

import io.circe.Codec
import monocle.macros.GenPrism
import nutria.core.languages.{Lambda, StringFunction, XAndLambda, ZAndLambda}

sealed trait Parameter {
  def name: String
  def value: Any
}

case class IntParameter(name: String, value: Int)                                                                         extends Parameter
case class FloatParameter(name: String, value: Float)                                                                     extends Parameter
case class RGBParameter(name: String, value: RGB)                                                                         extends Parameter
case class RGBAParameter(name: String, value: RGBA)                                                                       extends Parameter
case class FunctionParameter(name: String, value: StringFunction[ZAndLambda], includeDerivative: Boolean = false)         extends Parameter
case class InitialFunctionParameter(name: String, value: StringFunction[Lambda.type], includeDerivative: Boolean = false) extends Parameter
case class NewtonFunctionParameter(name: String, value: StringFunction[XAndLambda], includeDerivative: Boolean = false)   extends Parameter

object Parameter extends CirceCodec {
  val IntParameter             = GenPrism[Parameter, IntParameter]
  val FloatParameter           = GenPrism[Parameter, FloatParameter]
  val RGBParameter             = GenPrism[Parameter, RGBParameter]
  val RGBAParameter            = GenPrism[Parameter, RGBAParameter]
  val FunctionParameter        = GenPrism[Parameter, FunctionParameter]
  val InitialFunctionParameter = GenPrism[Parameter, InitialFunctionParameter]
  val NewtonFunctionParameter  = GenPrism[Parameter, NewtonFunctionParameter]

  implicit val codec: Codec[Parameter] = semiauto.deriveConfiguredCodec
}
