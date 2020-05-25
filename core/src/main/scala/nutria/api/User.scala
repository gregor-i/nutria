package nutria.api

import io.circe.Codec
import nutria.CirceCodec

case class User(id: String, name: String, email: String, googleUserId: Option[String], admin: Boolean = false)

object User extends CirceCodec {
  def isOwner(user: Option[User], fractal: WithId[_]): Boolean =
    user.exists(_.id == fractal.owner)

  implicit val codec: Codec[User] = semiauto.deriveConfiguredCodec
}
