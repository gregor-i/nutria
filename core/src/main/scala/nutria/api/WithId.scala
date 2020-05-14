package nutria.api

import io.circe.syntax._
import io.circe.{Codec, Decoder, Encoder}
import nutria.CirceCodec

@monocle.macros.Lenses()
case class WithId[A](id: String, owner: String, entity: A)

object WithId extends CirceCodec {
//  val viewports: Lens[WithId[FractalEntity], ViewportList] =
//    WithId[FractalEntity].entity.composeLens(FractalEntity.views)

  implicit def codec[A: Decoder: Encoder]: Codec[WithId[A]] = Codec.from(
    decodeA = Decoder[WithId[A]] { json =>
      for {
        entity <- json.as[A]
        id     <- json.downField("id").as[String]
        owner  <- json.downField("owner").as[String]
      } yield WithId(id, owner, entity)
    },
    encodeA = Encoder[WithId[A]] { row =>
      Encoder[A]
        .apply(row.entity)
        .mapObject(_.add("id", row.id.asJson))
        .mapObject(_.add("owner", row.owner.asJson))
    }
  )
}
