package nutria.api

import java.time.ZonedDateTime

import io.circe.{Codec, Decoder, Encoder}
import nutria.CirceCodec

@monocle.macros.Lenses()
case class WithId[A](id: String, owner: String, entity: A, updatedAt: ZonedDateTime, insertedAt: ZonedDateTime)

object WithId extends CirceCodec {
  implicit def codec[A: Encoder: Decoder]: Codec[WithId[A]] = semiauto.deriveConfiguredCodec[WithId[A]]

  implicit def ordering[A: Ordering]: Ordering[WithId[A]] = Ordering.by[WithId[A], ZonedDateTime](_.updatedAt).reverse
}
