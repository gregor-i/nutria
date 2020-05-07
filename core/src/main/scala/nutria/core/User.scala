package nutria.core

import io.circe.Codec

case class User(id: String, name: String, email: String, googleUserId: Option[String])

object User extends CirceCodec {
  def isOwner(user: Option[User], fractal: FractalEntityWithId): Boolean =
    user.exists(_.id == fractal.owner)

  implicit val codec: Codec[User] = semiauto.deriveConfiguredCodec
}
