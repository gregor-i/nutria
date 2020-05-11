package module.auth

import io.circe.Codec
import nutria.CirceCodec

case class GoogleUserInfo(id: String, name: String, email: String)

object GoogleUserInfo extends CirceCodec {
  implicit val codec: Codec[GoogleUserInfo] = semiauto.deriveConfiguredCodec
}
