package nutria.api

import io.circe.syntax._
import io.circe.{Codec, Decoder, Encoder, Json, JsonObject}
import nutria.CirceCodec

@monocle.macros.Lenses()
case class WithId[A](id: String, owner: String, entity: A)

object WithId extends CirceCodec {
  implicit def codec[A: Encoder: Decoder]: Codec[WithId[A]] = semiauto.deriveConfiguredCodec[WithId[A]]

  implicit def ordering[A: Ordering]: Ordering[WithId[A]] = Ordering.by(withId => (withId.entity, withId.id))
}
