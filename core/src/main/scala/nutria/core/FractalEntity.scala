package nutria.core

import eu.timepit.refined._
import eu.timepit.refined.api.Refined
import eu.timepit.refined.numeric.Positive
import io.circe.syntax._
import io.circe.{Codec, Decoder, Encoder}
import nutria.core.viewport.DefaultViewport


@monocle.macros.Lenses()
case class FractalEntity(program: FractalProgram,
                         view: Viewport = DefaultViewport.defaultViewport,
                         description: String = "",
                         reference: List[String] = List.empty,
                         antiAliase: Int Refined Positive = refineMV(2)
                        )


object FractalEntity extends CirceCodex {
  def id(fractalEntity: FractalEntity): String = fractalEntity.hashCode().toHexString.padTo(8, '0')

  implicit val codec: Codec[FractalEntity] = semiauto.deriveConfiguredCodec
}

@monocle.macros.Lenses()
case class FractalEntityWithId(id: String,
                               entity: FractalEntity)

object FractalEntityWithId extends CirceCodex {
  implicit val codec: Codec[FractalEntityWithId] = Codec.from(
    decodeA = Decoder[FractalEntityWithId] { json =>
      for {
        entity <- json.as[FractalEntity]
        id <- json.downField("id").as[String]
      } yield FractalEntityWithId(id, entity)
    },
    encodeA = Encoder[FractalEntityWithId] { row =>
      Encoder[FractalEntity].apply(row.entity)
        .mapObject(_.add("id", row.id.asJson))
    }
  )
}
