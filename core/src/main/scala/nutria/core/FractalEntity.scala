package nutria.core

import eu.timepit.refined._
import eu.timepit.refined.api.Refined
import eu.timepit.refined.collection.NonEmpty
import eu.timepit.refined.numeric.Positive
import io.circe.syntax._
import io.circe.{Codec, Decoder, Encoder}
import nutria.core.viewport.DefaultViewport


@monocle.macros.Lenses()
case class FractalEntity(title: String = "",
                         program: FractalProgram,
                         views: List[Viewport] Refined NonEmpty = refineV[NonEmpty](List(DefaultViewport.defaultViewport)).toOption.get,
                         description: String = "",
                         reference: List[String] = List.empty,
                         antiAliase: Int Refined Positive = refineMV(1)
                        )


object FractalEntity extends CirceCodex {
  implicit val ordering: Ordering[FractalEntity] = FractalProgram.ordering.on(_.program)

  implicit val codec: Codec[FractalEntity] = semiauto.deriveConfiguredCodec
}

@monocle.macros.Lenses()
case class FractalEntityWithId(id: String,
                               owner: String,
                               published: Boolean,
                               entity: FractalEntity)

object FractalEntityWithId extends CirceCodex {
  implicit val ordering: Ordering[FractalEntityWithId] = FractalProgram.ordering.on(_.entity.program)

  implicit val codec: Codec[FractalEntityWithId] = Codec.from(
    decodeA = Decoder[FractalEntityWithId] { json =>
      for {
        entity <- json.as[FractalEntity]
        id <- json.downField("id").as[String]
        owner <- json.downField("owner").as[String]
        published <- json.downField("published").as[Boolean]
      } yield FractalEntityWithId(id, owner, published, entity)
    },
    encodeA = Encoder[FractalEntityWithId] { row =>
      Encoder[FractalEntity].apply(row.entity)
        .mapObject(_.add("id", row.id.asJson))
        .mapObject(_.add("owner", row.owner.asJson))
        .mapObject(_.add("published", row.published.asJson))
    }
  )
}
