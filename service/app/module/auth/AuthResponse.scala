package module.auth

import io.circe.Codec
import nutria.core.CirceCodex

case class AuthResponse(access_token: String, expires_in: Int, token_type: String)

object AuthResponse extends CirceCodex{
  implicit val codec: Codec[AuthResponse] = semiauto.deriveConfiguredCodec
}
