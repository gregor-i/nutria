package nutria.core

import io.circe.Codec
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
  implicit val codec: Codec[Parameter] = semiauto.deriveConfiguredCodec
}
