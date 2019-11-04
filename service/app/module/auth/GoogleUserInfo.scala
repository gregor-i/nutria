package module.auth

import io.circe.Codec
import nutria.core.CirceCodex

case class GoogleUserInfo(id: String, name: String, email: String, picture: String)

object GoogleUserInfo extends CirceCodex{
  implicit val codec: Codec[GoogleUserInfo] = semiauto.deriveConfiguredCodec
}