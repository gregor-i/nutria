package nutria.core

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder, JsonObject}
import io.circe.syntax._

@monocle.macros.Lenses()
case class FractalEntity(program: FractalProgram,
                         view: Viewport = DefaultViewport.defaultViewport,
                         description: String = "",
                         reference: Option[String] = None,
                        )


object FractalEntity {
  def id(fractalEntity: FractalEntity): String = fractalEntity.hashCode().toHexString

  implicit val encodeViewport: Encoder[Viewport] = deriveEncoder
  implicit val decodeViewport: Decoder[Viewport] = deriveDecoder

  implicit val decoder: Decoder[FractalEntity] = deriveDecoder
  implicit val encoder: Encoder[FractalEntity] = deriveEncoder
}

@monocle.macros.Lenses()
case class FractalEntityWithId(id: String,
                               entity: FractalEntity)

object FractalEntityWithId {
  implicit val encoder: Encoder[FractalEntityWithId] = Encoder[FractalEntityWithId] { row =>
    Encoder[FractalEntity].apply(row.entity)
      .deepMerge(JsonObject("id" -> row.id.asJson).asJson)
  }

  implicit val decode: Decoder[FractalEntityWithId] = Decoder[FractalEntityWithId] { json =>
    for {
      entity <- json.as[FractalEntity]
      id <- json.downField("id").as[String]
    } yield FractalEntityWithId(id, entity)
  }
}
