package nutria.api

import io.circe.{Codec, Decoder, Encoder}
import nutria.CirceCodec
import io.circe.syntax._

@monocle.macros.Lenses()
case class Entity[A](
    title: String = "<no title>",
    description: String = "",
    reference: List[String] = List.empty,
    published: Boolean = false,
    value: A
)

object Entity extends CirceCodec {
  implicit def codec[A: Decoder: Encoder]: Codec[Entity[A]] = Codec.from(
    decodeA = Decoder[Entity[A]] { json =>
      for {
        value       <- json.as[A]
        title       <- json.downField("title").as[String]
        description <- json.downField("description").as[String]
        reference   <- json.downField("reference").as[List[String]]
        published   <- json.downField("published").as[Boolean]
      } yield Entity(title, description, reference, published, value)
    },
    encodeA = Encoder[Entity[A]] { row =>
      Encoder[A]
        .apply(row.value)
        .mapObject(_.add("title", row.title.asJson))
        .mapObject(_.add("description", row.description.asJson))
        .mapObject(_.add("reference", row.reference.asJson))
        .mapObject(_.add("published", row.published.asJson))
    }
  )

  implicit def ordering[A: Ordering]: Ordering[Entity[A]] = Ordering.by(_.value)
}
