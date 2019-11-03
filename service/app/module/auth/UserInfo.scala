package module.auth

import io.circe.Codec
import nutria.core.CirceCodex

case class UserInfo(id: String, email: String, picture: String)

object UserInfo extends CirceCodex{
  implicit val codec: Codec[UserInfo] = semiauto.deriveConfiguredCodec
}