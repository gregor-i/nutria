package nutria.api

import io.circe.Codec
import nutria.CirceCodec
import nutria.core.{FractalEntity, WithId}

case class User(id: String, name: String, email: String, googleUserId: Option[String])

object User extends CirceCodec {
  def isOwner(user: Option[User], fractal: WithId[FractalEntity]): Boolean =
    user.exists(_.id == fractal.owner)

  implicit val codec: Codec[User] = semiauto.deriveConfiguredCodec
}
