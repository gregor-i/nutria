package nutria.core

import io.circe.Codec
import monocle.Lens

sealed trait Parameter {
  def name: String
  def literal: String
}

case class StringParameter(name: String, literal: String) extends Parameter

object Parameter extends CirceCodex {
  val literal = Lens[Parameter, String](_.literal)(
    literal => parameter => StringParameter(parameter.name, literal)
  )

  implicit val codec: Codec[Parameter] = semiauto.deriveConfiguredCodec
}
