package module.auth

import io.circe.Codec
import nutria.CirceCodec

case class AuthResponse(access_token: String, expires_in: Int, token_type: String)

object AuthResponse extends CirceCodec {
  implicit val codec: Codec[AuthResponse] = semiauto.deriveConfiguredCodec
}
