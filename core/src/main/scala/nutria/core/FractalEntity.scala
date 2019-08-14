package nutria.core

import eu.timepit.refined._
import eu.timepit.refined.api.Refined
import eu.timepit.refined.numeric.Positive
import io.circe.syntax._
import io.circe.{Decoder, Encoder}
import nutria.core.viewport.DefaultViewport

@monocle.macros.Lenses()
case class FractalEntity(program: FractalProgram,
                         view: Viewport = DefaultViewport.defaultViewport,
                         description: String = "",
                         reference: Option[String] = None,
                         antiAliase: Int Refined Positive = refineMV(2)
                        )


object FractalEntity extends CirceCodex {
  def id(fractalEntity: FractalEntity): String = fractalEntity.hashCode().toHexString.padTo(8, '0')

  implicit val encodeViewport: Encoder[Viewport] = semiauto.deriveEncoder
  implicit val decodeViewport: Decoder[Viewport] = semiauto.deriveDecoder

  implicit val decoder: Decoder[FractalEntity] = semiauto.deriveDecoder
  implicit val encoder: Encoder[FractalEntity] = semiauto.deriveEncoder
}

@monocle.macros.Lenses()
case class FractalEntityWithId(id: String,
                               entity: FractalEntity)

object FractalEntityWithId extends CirceCodex {
  implicit val encoder: Encoder[FractalEntityWithId] = Encoder[FractalEntityWithId] { row =>
    Encoder[FractalEntity].apply(row.entity)
      .mapObject(_.add("id", row.id.asJson))
  }

  implicit val decode: Decoder[FractalEntityWithId] = Decoder[FractalEntityWithId] { json =>
    for {
      entity <- json.as[FractalEntity]
      id <- json.downField("id").as[String]
    } yield FractalEntityWithId(id, entity)
  }
}
