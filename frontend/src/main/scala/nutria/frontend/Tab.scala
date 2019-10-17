package nutria.frontend

import io.circe.{Codec, Decoder, Encoder}
import nutria.core.CirceCodex

sealed trait Tab
case object General extends Tab
case object Template extends Tab
case object Parameters extends Tab
case object Snapshots extends Tab

object Tab extends CirceCodex{
  def default = General

  def fromString(s: String): Option[Tab] = list.find(toString(_) == s)

  def toString(t: Tab): String = t.toString

  def list: List[Tab] = List(General, Template, Parameters, Snapshots)

  implicit val codec: Codec[Tab] = Codec.from(
    decodeA = Decoder[String].emap(s => fromString(s).toRight("")),
    encodeA = Encoder[String].contramap(toString)
  )
}