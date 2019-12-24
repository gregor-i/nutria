package nutria.core

import io.circe.Codec

case class User(id: String, name: String, email: String, picture: String)

object User extends CirceCodex {
  implicit val codec: Codec[User] = semiauto.deriveConfiguredCodec
}
